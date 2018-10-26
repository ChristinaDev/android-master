package com.daniel.alexa.baker.services.youtube.bakers;


import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.daniel.alexa.baker.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.channel.ChannelBaker;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.linkhandler.ListLinkHandler;
import com.daniel.alexa.baker.stream.StreamInfoItem;
import com.daniel.alexa.baker.stream.StreamInfoItemsCollector;
import com.daniel.alexa.baker.utils.DonationLinkHelper;
import com.daniel.alexa.baker.utils.Parser;
import com.daniel.alexa.baker.utils.Utils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class YoutubeChannelBaker extends ChannelBaker {
    private static final String CHANNEL_FEED_BASE = "https://www.youtube.com/feeds/videos.xml?channel_id=";
    private static final String CHANNEL_URL_PARAMETERS = "/videos?view=0&flow=list&sort=dd&live_view=10000";

    private Document doc;

    public YoutubeChannelBaker(StreamingService service, ListLinkHandler urlIdHandler) {
        super(service, urlIdHandler);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        String channelUrl = super.getUrl() + CHANNEL_URL_PARAMETERS;
        String pageContent = downloader.download(channelUrl);
        doc = Jsoup.parse(pageContent, channelUrl);
    }

    @Override
    public String getNextPageUrl() throws ExtractionException {
        return getNextPageUrlFrom(doc);
    }

    @Nonnull
    @Override
    public String getUrl() throws ParsingException {
        try {
            return "https://www.youtube.com/channel/" + getId();
        } catch (ParsingException e) {
            return super.getUrl();
        }
    }

    @Nonnull
    @Override
    public String getId() throws ParsingException {
        try {
            Element element = doc.getElementsByClass("yt-uix-subscription-button").first();
            if (element == null) element = doc.getElementsByClass("yt-uix-subscription-preferences-button").first();

            return element.attr("data-channel-external-id");
        } catch (Exception e) {
            throw new ParsingException("Could not get channel id", e);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        try {
            return doc.select("meta[property=\"og:title\"]").first().attr("content");
        } catch (Exception e) {
            throw new ParsingException("Could not get channel name", e);
        }
    }

    @Override
    public String getAvatarUrl() throws ParsingException {
        try {
            return doc.select("img[class=\"channel-header-profile-image\"]").first().attr("abs:src");
        } catch (Exception e) {
            throw new ParsingException("Could not get avatar", e);
        }
    }

    @Override
    public String getBannerUrl() throws ParsingException {
        try {
            Element el = doc.select("div[id=\"gh-banner\"]").first().select("style").first();
            String cssContent = el.html();
            String url = "https:" + Parser.matchGroup1("url\\(([^)]+)\\)", cssContent);

            return url.contains("s.ytimg.com") || url.contains("default_banner") ? null : url;
        } catch (Exception e) {
            throw new ParsingException("Could not get Banner", e);
        }
    }

    @Override
    public String getFeedUrl() throws ParsingException {
        try {
            return CHANNEL_FEED_BASE + getId();
        } catch (Exception e) {
            throw new ParsingException("Could not get feed url", e);
        }
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        final Element el = doc.select("span[class*=\"yt-subscription-button-subscriber-count\"]").first();
        if (el != null) {
            try {
                return Long.parseLong(Utils.removeNonDigitCharacters(el.text()));
            } catch (NumberFormatException e) {
                throw new ParsingException("Could not get subscriber count", e);
            }
        } else {
           
            return -1;
        }
    }

    @Override
    public String getDescription() throws ParsingException {
        try {
            return doc.select("meta[name=\"description\"]").first().attr("content");
        } catch (Exception e) {
            throw new ParsingException("Could not get channel description", e);
        }
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws ExtractionException {
        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        Element ul = doc.select("ul[id=\"browse-items-primary\"]").first();
        collectStreamsFrom(collector, ul);
        return new InfoItemsPage<>(collector, getNextPageUrl());
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(String pageUrl) throws IOException, ExtractionException {
        if (pageUrl == null || pageUrl.isEmpty()) {
            throw new ExtractionException(new IllegalArgumentException("Page url is empty or null"));
        }

        fetchPage();

        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        JsonObject ajaxJson;
        try {
            ajaxJson = JsonParser.object().from(Alexa.getDownloader().download(pageUrl));
        } catch (JsonParserException pe) {
            throw new ParsingException("Could not parse json data for next streams", pe);
        }

        final Document ajaxHtml = Jsoup.parse(ajaxJson.getString("content_html"), pageUrl);
        collectStreamsFrom(collector, ajaxHtml.select("body").first());

        return new InfoItemsPage<>(collector, getNextPageUrlFromAjaxPage(ajaxJson, pageUrl));
    }

    @Override
    public String[] getDonationLinks() throws ParsingException {
        try {
            ArrayList<String> links = new ArrayList<>();
            Element linkHolder = doc.select("div[id=\"header-links\"]").first();
            if(linkHolder == null) {
                // this occures if no links are embeded into the channel
                return new String[0];
            }
            for(Element a : linkHolder.select("a")) {
                String link = a.attr("abs:href");
                if(DonationLinkHelper.getDonatoinServiceByLink(link) != DonationLinkHelper.DonationService.NO_DONATION) {
                    links.add(link);
                }
            }
            String[] retLinks = new String[links.size()];
            retLinks = links.toArray(retLinks);
            return retLinks;
        } catch (Exception e) {
            throw new ParsingException("Could not get donation links", e);
        }
    }

    private String getNextPageUrlFromAjaxPage(final JsonObject ajaxJson, final String pageUrl)
        throws ParsingException {
        String loadMoreHtmlDataRaw = ajaxJson.getString("load_more_widget_html");
        if (!loadMoreHtmlDataRaw.isEmpty()) {
            return getNextPageUrlFrom(Jsoup.parse(loadMoreHtmlDataRaw, pageUrl));
        } else {
            return "";
        }
    }

    private String getNextPageUrlFrom(Document d) throws ParsingException {
        try {
            Element button = d.select("button[class*=\"yt-uix-load-more\"]").first();
            if (button != null) {
                return button.attr("abs:data-uix-load-more-href");
            } else {
           
                return "";
            }
        } catch (Exception e) {
            throw new ParsingException("Could not get next page url", e);
        }
    }

    private void collectStreamsFrom(StreamInfoItemsCollector collector, Element element) throws ParsingException {
        collector.reset();

        final String uploaderName = getName();
        final String uploaderUrl = getUrl();
        for (final Element li : element.children()) {
            if (li.select("div[class=\"feed-item-dismissable\"]").first() != null) {
                collector.commit(new YoutubeStreamInfoItemBaker(li) {
                    @Override
                    public String getUrl() throws ParsingException {
                        try {
                            Element el = li.select("div[class=\"feed-item-dismissable\"]").first();
                            Element dl = el.select("h3").first().select("a").first();
                            return dl.attr("abs:href");
                        } catch (Exception e) {
                            throw new ParsingException("Could not get web page url for the video", e);
                        }
                    }

                    @Override
                    public String getName() throws ParsingException {
                        try {
                            Element el = li.select("div[class=\"feed-item-dismissable\"]").first();
                            Element dl = el.select("h3").first().select("a").first();
                            return dl.text();
                        } catch (Exception e) {
                            throw new ParsingException("Could not get title", e);
                        }
                    }

                    @Override
                    public String getUploaderName() throws ParsingException {
                        return uploaderName;
                    }

                    @Override
                    public String getUploaderUrl() throws ParsingException {
                        return uploaderUrl;
                    }

                    @Override
                    public String getThumbnailUrl() throws ParsingException {
                        try {
                            String url;
                            Element te = li.select("span[class=\"yt-thumb-clip\"]").first()
                                    .select("img").first();
                            url = te.attr("abs:src");
                        
                            if (url.contains(".gif")) {
                                url = te.attr("abs:data-thumb");
                            }
                            return url;
                        } catch (Exception e) {
                            throw new ParsingException("Could not get thumbnail url", e);
                        }
                    }
                });
            }
        }
    }
}
