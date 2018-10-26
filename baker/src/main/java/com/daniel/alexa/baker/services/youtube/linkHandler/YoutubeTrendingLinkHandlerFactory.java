package com.daniel.alexa.baker.services.youtube.linkHandler;

import com.daniel.alexa.baker.linkhandler.ListLinkHandlerFactory;
import com.daniel.alexa.baker.utils.Parser;

import java.util.List;

public class YoutubeTrendingLinkHandlerFactory extends ListLinkHandlerFactory {

    public String getUrl(String id, List<String> contentFilters, String sortFilter) {
        return "https://www.youtube.com/feed/trending";
    }

    @Override
    public String getId(String url) {
        return "Trending";
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        return Parser.isMatch("^(https://|http://|)(www.|m.|)youtube.com/feed/trending(|\\?.*)$", url);
    }
}
