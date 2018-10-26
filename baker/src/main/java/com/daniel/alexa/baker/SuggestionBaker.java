package com.daniel.alexa.baker;

import com.daniel.alexa.baker.exceptions.ExtractionException;

import java.io.IOException;
import java.util.List;



public abstract class SuggestionBaker {

    private final int serviceId;

    public SuggestionBaker(int serviceId) {
        this.serviceId = serviceId;
    }

    public abstract List<String> suggestionList(String query, String contentCountry) throws IOException, ExtractionException;

    public int getServiceId() {
        return serviceId;
    }
}
