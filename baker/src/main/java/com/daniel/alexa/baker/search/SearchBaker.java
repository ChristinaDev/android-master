package com.daniel.alexa.baker.search;

import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.linkhandler.SearchQueryHandler;

public abstract class SearchBaker extends ListBaker<InfoItem> {

    public static class NothingFoundException extends ExtractionException {
        public NothingFoundException(String message) {
            super(message);
        }
    }

    private final InfoItemsSearchCollector collector;
    private final String contentCountry;

    public SearchBaker(StreamingService service, SearchQueryHandler urlIdHandler, String contentCountry) {
        super(service, urlIdHandler);
        collector = new InfoItemsSearchCollector(service.getServiceId());
        this.contentCountry = contentCountry;
    }

    public String getSearchString() {
        return getUIHandler().getSearchString();
    }

    public abstract String getSearchSuggestion() throws ParsingException;

    protected InfoItemsSearchCollector getInfoItemSearchCollector() {
        return collector;
    }

    @Override
    public SearchQueryHandler getUIHandler() {
        return (SearchQueryHandler) super.getUIHandler();
    }

    @Override
    public String getName() {
        return getUIHandler().getSearchString();
    }

    protected String getContentCountry() {
        return contentCountry;
    }
}
