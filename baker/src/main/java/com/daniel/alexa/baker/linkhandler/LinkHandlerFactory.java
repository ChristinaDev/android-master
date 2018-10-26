package com.daniel.alexa.baker.linkhandler;

import com.daniel.alexa.baker.exceptions.FoundAdException;
import com.daniel.alexa.baker.exceptions.ParsingException;


public abstract class LinkHandlerFactory {



    public abstract String getId(String url) throws ParsingException;
    public abstract String getUrl(String id) throws ParsingException;
    public abstract boolean onAcceptUrl(final String url) throws ParsingException;



    public LinkHandler fromUrl(String url) throws ParsingException {
        if(url == null) throw new IllegalArgumentException("url can not be null");
        if(!acceptUrl(url)) {
            throw new ParsingException("Malformed unacceptable url: " + url);
        }

        final String id = getId(url);
        return new LinkHandler(url, getUrl(id), id);
    }

    public LinkHandler fromId(String id) throws ParsingException {
        if(id == null) throw new IllegalArgumentException("id can not be null");
        final String url = getUrl(id);
        return new LinkHandler(url, url, id);
    }

 
    public boolean acceptUrl(final String url) throws ParsingException {
        try {
            return onAcceptUrl(url);
        } catch (FoundAdException fe) {
            throw fe;
        }
    }
}
