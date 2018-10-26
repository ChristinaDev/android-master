package com.daniel.alexa.baker.services.youtube.bakers;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.daniel.alexa.baker.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.SuggestionBaker;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class YoutubeSuggestionBaker extends SuggestionBaker {

    public static final String CHARSET_UTF_8 = "UTF-8";

    public YoutubeSuggestionBaker(int serviceId) {
        super(serviceId);
    }

    @Override
    public List<String> suggestionList(String query, String contentCountry) throws IOException, ExtractionException {
        Downloader dl = Alexa.getDownloader();
        List<String> suggestions = new ArrayList<>();

        String url = "https://suggestqueries.google.com/complete/search"
                + "?client=" + "youtube" //"firefox" for JSON, 'toolbar' for xml
                + "&jsonp=" + "JP"
                + "&ds=" + "yt"
                + "&hl=" + URLEncoder.encode(contentCountry, CHARSET_UTF_8)
                + "&q=" + URLEncoder.encode(query, CHARSET_UTF_8);

        String response = dl.download(url);
        // trim JSONP part "JP(...)"
        response = response.substring(3, response.length()-1);
        try {
            JsonArray collection = JsonParser.array().from(response).getArray(1, new JsonArray());
            for (Object suggestion : collection) {
                if (!(suggestion instanceof JsonArray)) continue;
                String suggestionStr = ((JsonArray)suggestion).getString(0);
                if (suggestionStr == null) continue;
                suggestions.add(suggestionStr);
            }

            return suggestions;
        } catch (JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }
    }
}
