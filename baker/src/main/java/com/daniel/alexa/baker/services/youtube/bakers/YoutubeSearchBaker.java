package com.daniel.alexa.baker.services.youtube.bakers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.daniel.alexa.baker.Downloader;
import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.search.InfoItemsSearchCollector;
import com.daniel.alexa.baker.search.SearchBaker;
import com.daniel.alexa.baker.linkhandler.SearchQueryHandler;
import com.daniel.alexa.baker.utils.Parser;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class YoutubeSearchBaker extends SearchBaker {

    private Document doc;

    public YoutubeSearchBaker(StreamingService service,
                                  SearchQueryHandler urlIdHandler,
                                  String contentCountry) {
        super(service, urlIdHandler, contentCountry);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        final String site;
        final String url = getUrl();
        final String contentCountry = getContentCountry();
   
        if (!contentCountry.isEmpty()) {
      
            site = downloader.download(url, contentCountry);
        } else {
            site = downloader.download(url);
        }

        doc = Jsoup.parse(site, url);
    }

    @Override
    public String getSearchSuggestion() throws ParsingException {
        final Element el = doc.select("div[class*=\"spell-correction\"]").first();
        if (el != null) {
            return el.select("a").first().text();
        } else {
            return "";
        }
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return new InfoItemsPage<>(collectItems(doc), getNextPageUrl());
    }

    @Override
    public String getNextPageUrl() throws ExtractionException {
        return getUrl() + "&page=" + Integer.toString( 2);
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(String pageUrl) throws IOException, ExtractionException {
        String site = getDownloader().download(pageUrl);
        doc = Jsoup.parse(site, pageUrl);

        return new InfoItemsPage<>(collectItems(doc), getNextPageUrlFromCurrentUrl(pageUrl));
    }

    private String getNextPageUrlFromCurrentUrl(String currentUrl)
            throws MalformedURLException, UnsupportedEncodingException {
        final int pageNr = Integer.parseInt(
                Parser.compatParseMap(
                        new URL(currentUrl)
                                .getQuery())
                        .get("page"));

        return currentUrl.replace("&page=" + Integer.toString( pageNr),
                "&page=" + Integer.toString(pageNr + 1));
    }

    private InfoItemsSearchCollector collectItems(Document doc) throws NothingFoundException  {
        InfoItemsSearchCollector collector = getInfoItemSearchCollector();

        Element list = doc.select("ol[class=\"item-section\"]").first();

        for (Element item : list.children()) {
          

            Element el;

            if ((el = item.select("div[class*=\"search-message\"]").first()) != null) {
                throw new NothingFoundException(el.text());

            } else if ((el = item.select("div[class*=\"yt-lockup-video\"]").first()) != null) {
                collector.commit(new YoutubeStreamInfoItemBaker(el));
            } else if ((el = item.select("div[class*=\"yt-lockup-channel\"]").first()) != null) {
                collector.commit(new YoutubeChannelInfoItemBaker(el));
            } else if ((el = item.select("div[class*=\"yt-lockup-playlist\"]").first()) != null &&
                    item.select(".yt-pl-icon-mix").isEmpty()) {
                collector.commit(new YoutubePlaylistInfoItemBaker(el));
            }
        }

        return collector;
    }
}
