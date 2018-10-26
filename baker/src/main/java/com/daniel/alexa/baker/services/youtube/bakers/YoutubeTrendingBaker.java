package com.daniel.alexa.baker.services.youtube.bakers;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.daniel.alexa.baker.Downloader;
import com.daniel.alexa.baker.linkhandler.ListLinkHandler;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.kiosk.KioskBaker;
import com.daniel.alexa.baker.stream.StreamInfoItem;
import com.daniel.alexa.baker.stream.StreamInfoItemsCollector;

import javax.annotation.Nonnull;
import java.io.IOException;

public class YoutubeTrendingBaker extends KioskBaker {

    private Document doc;

    public YoutubeTrendingBaker(StreamingService service,
                                    ListLinkHandler urlIdHandler,
                                    String kioskId) {
        super(service, urlIdHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        final String contentCountry = getContentCountry();
        String url = getUrl();
        if(contentCountry != null && !contentCountry.isEmpty()) {
            url += "?gl=" + contentCountry;
        }

        String pageContent = downloader.download(url);
        doc = Jsoup.parse(pageContent, url);
    }

    @Override
    public String getNextPageUrl() {
        return "";
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(String pageUrl) {
        return null;
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        try {
            Element a = doc.select("a[href*=\"/feed/trending\"]").first();
            Element span = a.select("span[class*=\"display-name\"]").first();
            Element nameSpan = span.select("span").first();
            return nameSpan.text();
        } catch (Exception e) {
            throw new ParsingException("Could not get Trending name", e);
        }
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws ParsingException {
        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        Elements uls = doc.select("ul[class*=\"expanded-shelf-content-list\"]");
        for(Element ul : uls) {
            for(final Element li : ul.children()) {
                final Element el = li.select("div[class*=\"yt-lockup-dismissable\"]").first();
                collector.commit(new YoutubeStreamInfoItemBaker(li) {
                    @Override
                    public String getUrl() throws ParsingException {
                        try {
                            Element dl = el.select("h3").first().select("a").first();
                            return dl.attr("abs:href");
                        } catch (Exception e) {
                            throw new ParsingException("Could not get web page url for the video", e);
                        }
                    }

                    @Override
                    public String getName() throws ParsingException {
                        try {
                            Element dl = el.select("h3").first().select("a").first();
                            return dl.text();
                        } catch (Exception e) {
                            throw new ParsingException("Could not get web page url for the video", e);
                        }
                    }

                    @Override
                    public String getUploaderUrl() throws ParsingException {
                        try {
                            String link = getUploaderLink().attr("abs:href");
                            if (link.isEmpty()) {
                                throw new IllegalArgumentException("is empty");
                            }
                            return link;
                        } catch (Exception e) {
                            throw new ParsingException("Could not get Uploader name");
                        }
                    }

                    private Element getUploaderLink() {
                        Element uploaderEl = el.select("div[class*=\"yt-lockup-byline \"]").first();
                        return uploaderEl.select("a").first();
                    }

                    @Override
                    public String getUploaderName() throws ParsingException {
                        try {
                            return getUploaderLink().text();
                        } catch (Exception e) {
                            throw new ParsingException("Could not get Uploader name");
                        }
                    }

                    @Override
                    public String getThumbnailUrl() throws ParsingException {
                        try {
                            String url;
                            Element te = li.select("span[class=\"yt-thumb-simple\"]").first()
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

        return new InfoItemsPage<>(collector, getNextPageUrl());
    }
}
