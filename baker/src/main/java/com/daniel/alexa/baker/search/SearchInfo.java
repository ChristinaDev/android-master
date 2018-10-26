package com.daniel.alexa.baker.search;

import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.ListInfo;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.linkhandler.SearchQueryHandler;
import com.daniel.alexa.baker.utils.BakerHelper;

import java.io.IOException;


public class SearchInfo extends ListInfo<InfoItem> {

    private String searchString;
    private String searchSuggestion;

    public SearchInfo(int serviceId,
                      SearchQueryHandler qIHandler,
                      String searchString) {
        super(serviceId, qIHandler, "Search");
        this.searchString = searchString;
    }


    public static SearchInfo getInfo(StreamingService service, SearchQueryHandler searchQuery, String contentCountry) throws ExtractionException, IOException {
        SearchBaker baker = service.getSearchBaker(searchQuery, contentCountry);
        baker.fetchPage();
        return getInfo(baker);
    }

    public static SearchInfo getInfo(SearchBaker baker) throws ExtractionException, IOException {
        final SearchInfo info = new SearchInfo(
                baker.getServiceId(),
                baker.getUIHandler(),
                baker.getSearchString());

        try {
            info.searchSuggestion = baker.getSearchSuggestion();
        } catch (Exception e) {
            info.addError(e);
        }

        ListBaker.InfoItemsPage<InfoItem> page = BakerHelper.getItemsPageOrLogError(info, baker);
        info.setRelatedItems(page.getItems());
        info.setNextPageUrl(page.getNextPageUrl());

        return info;
    }


    public static ListBaker.InfoItemsPage<InfoItem> getMoreItems(StreamingService service,
                                                                     SearchQueryHandler query,
                                                                     String contentCountry,
                                                                     String pageUrl)
            throws IOException, ExtractionException {
        return service.getSearchBaker(query, contentCountry).getPage(pageUrl);
    }

    // Getter
    public String getSearchString() {
        return searchString;
    }

    public String getSearchSuggestion() {
        return searchSuggestion;
    }
}
