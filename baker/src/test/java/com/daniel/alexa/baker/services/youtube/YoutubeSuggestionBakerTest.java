package com.daniel.alexa.baker.services.youtube;


import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.SuggestionBaker;
import com.daniel.alexa.baker.exceptions.ExtractionException;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static com.daniel.alexa.baker.ServiceList.YouTube;

/**
 * Test for {@link SuggestionBaker}
 */
public class YoutubeSuggestionBakerTest {
    private static SuggestionBaker suggestionBaker;

    @BeforeClass
    public static void setUp() throws Exception {
        Alexa.init(Downloader.getInstance());
        suggestionBaker = YouTube.getSuggestionBaker();
    }

    @Test
    public void testIfSuggestions() throws IOException, ExtractionException {
        assertFalse(suggestionBaker.suggestionList("hello", "de").isEmpty());
    }
}
