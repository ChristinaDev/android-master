package com.daniel.alexa.baker.linkhandler;

import com.daniel.alexa.baker.exceptions.ParsingException;

import java.util.ArrayList;
import java.util.List;

public abstract class ListLinkHandlerFactory extends LinkHandlerFactory {



    public List<String> getContentFilter(String url) throws ParsingException { return new ArrayList<>(0);}
    public String getSortFilter(String url) throws ParsingException {return ""; }
    public abstract String getUrl(String id, List<String> contentFilter, String sortFilter) throws ParsingException;



    @Override
    public ListLinkHandler fromUrl(String url) throws ParsingException {
        if(url == null) throw new IllegalArgumentException("url may not be null");

        return new ListLinkHandler(super.fromUrl(url), getContentFilter(url), getSortFilter(url));
    }

    @Override
    public ListLinkHandler fromId(String id) throws ParsingException {
        return new ListLinkHandler(super.fromId(id), new ArrayList<String>(0), "");
    }

    public ListLinkHandler fromQuery(String id,
                                     List<String> contentFilters,
                                     String sortFilter) throws ParsingException {
        final String url = getUrl(id, contentFilters, sortFilter);
        return new ListLinkHandler(url, url, id, contentFilters, sortFilter);
    }


    /**
     * @param id
     * @return the url coresponding to id without any filters applied
     */
    public String getUrl(String id) throws ParsingException {
        return getUrl(id, new ArrayList<String>(0), "");
    }

    /**
     * @return filter that can be applied when building a query for getting a list
     */
    public String[] getAvailableContentFilter() {
        return new String[0];
    }

    /**
     * @return filter that can be applied when building a query for getting a list
     */
    public String[] getAvailableSortFilter() {
        return new String[0];
    }
}
