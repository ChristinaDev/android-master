package com.daniel.alexa.baker.services.youtube.linkHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.daniel.alexa.baker.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.linkhandler.LinkHandlerFactory;
import com.daniel.alexa.baker.exceptions.FoundAdException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.exceptions.ReCaptchaException;
import com.daniel.alexa.baker.utils.Parser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;


public class YoutubeStreamLinkHandlerFactory extends LinkHandlerFactory {

    private static final YoutubeStreamLinkHandlerFactory instance = new YoutubeStreamLinkHandlerFactory();
    private static final String ID_PATTERN = "([\\-a-zA-Z0-9_]{11})";

    private YoutubeStreamLinkHandlerFactory() {
    }

    public static YoutubeStreamLinkHandlerFactory getInstance() {
        return instance;
    }

    @Override
    public String getUrl(String id) {
        return "https://www.youtube.com/watch?v=" + id;
    }

    @Override
    public String getId(String url) throws ParsingException, IllegalArgumentException {
        if (url.isEmpty()) {
            throw new IllegalArgumentException("The url parameter should not be empty");
        }

        String lowercaseUrl = url.toLowerCase();
        if (lowercaseUrl.contains("youtube")) {
            if (lowercaseUrl.contains("list=")) {
                throw new ParsingException("Error no suitable url: " + url);
            }
            if (url.contains("attribution_link")) {
                try {
                    String escapedQuery = Parser.matchGroup1("u=(.[^&|$]*)", url);
                    String query = URLDecoder.decode(escapedQuery, "UTF-8");
                    return Parser.matchGroup1("v=" + ID_PATTERN, query);
                } catch (UnsupportedEncodingException uee) {
                    throw new ParsingException("Could not parse attribution_link", uee);
                }
            }
            if (url.contains("vnd.youtube")) {
                return Parser.matchGroup1(ID_PATTERN, url);
            }
            if (url.contains("embed")) {
                return Parser.matchGroup1("embed/" + ID_PATTERN, url);
            }
            if (url.contains("googleads")) {
                throw new FoundAdException("Error found add: " + url);
            }
            return Parser.matchGroup1("[?&]v=" + ID_PATTERN, url);
        }
        if (lowercaseUrl.contains("youtu.be")) {
            if (lowercaseUrl.contains("list=")) {
                throw new ParsingException("Error no suitable url: " + url);
            }
            if (url.contains("v=")) {
                return Parser.matchGroup1("v=" + ID_PATTERN, url);
            }
            return Parser.matchGroup1("[Yy][Oo][Uu][Tt][Uu]\\.[Bb][Ee]/" + ID_PATTERN, url);
        }
        if (lowercaseUrl.contains("hooktube")) {
            if (lowercaseUrl.contains("&v=")
                    || lowercaseUrl.contains("?v=")) {
                return Parser.matchGroup1("[?&]v=" + ID_PATTERN, url);
            }
            if (url.contains("/embed/")) {
                return Parser.matchGroup1("embed/" + ID_PATTERN, url);
            }
            if (url.contains("/v/")) {
                return Parser.matchGroup1("v/" + ID_PATTERN, url);
            }
            if (url.contains("/watch/")) {
                return Parser.matchGroup1("watch/" + ID_PATTERN, url);
            }
        }
        throw new ParsingException("Error no suitable url: " + url);
    }

    @Override
    public boolean onAcceptUrl(final String url) throws FoundAdException {
        final String lowercaseUrl = url.toLowerCase();
        if (!lowercaseUrl.contains("youtube")  &&
            !lowercaseUrl.contains("youtu.be") &&
            !lowercaseUrl.contains("hooktube")) {
            return false;
     
        }
        try {
            getId(url);
            return true;
        } catch (FoundAdException fe) {
            throw fe;
        } catch (ParsingException e) {
            return false;
        }
    }
}
