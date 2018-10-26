package com.daniel.alexa.baker.services.youtube.linkHandler;

import com.daniel.alexa.baker.linkhandler.ListLinkHandlerFactory;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.utils.Parser;

import java.util.List;


public class YoutubeChannelLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final YoutubeChannelLinkHandlerFactory instance = new YoutubeChannelLinkHandlerFactory();
    private static final String ID_PATTERN = "/(user/[A-Za-z0-9_-]*|channel/[A-Za-z0-9_-]*)";

    public static YoutubeChannelLinkHandlerFactory getInstance() {
        return instance;
    }

    @Override
    public String getId(String url) throws ParsingException {
        return Parser.matchGroup1(ID_PATTERN, url);
    }

    @Override
    public String getUrl(String id, List<String> contentFilters, String searchFilter) {
        return "https://www.youtube.com/" + id;
    }

    @Override
    public boolean onAcceptUrl(String url) {
        return (url.contains("youtube") || url.contains("youtu.be") || url.contains("hooktube.com"))
                && (url.contains("/user/") || url.contains("/channel/"));
    }
}
