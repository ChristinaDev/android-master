package com.daniel.alexa.baker.services.youtube;


import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.services.youtube.bakers.YoutubeTrendingBaker;
import com.daniel.alexa.baker.services.youtube.linkHandler.YoutubeTrendingLinkHandlerFactory;
import com.daniel.alexa.baker.stream.StreamInfoItem;
import com.daniel.alexa.baker.utils.Utils;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;
import static com.daniel.alexa.baker.BakerAsserts.assertEmptyErrors;
import static com.daniel.alexa.baker.ServiceList.YouTube;


/**
 * Test for {@link YoutubeTrendingLinkHandlerFactory}
 */
public class YoutubeTrendingBakerTest {

    static YoutubeTrendingBaker baker;

    @BeforeClass
    public static void setUp() throws Exception {
        Alexa.init(Downloader.getInstance());
        baker = (YoutubeTrendingBaker) YouTube
                .getKioskList()
                .getBakerById("Trending", null);
        baker.fetchPage();
        baker.setContentCountry("de");
    }

    @Test
    public void testGetDownloader() throws Exception {
        assertNotNull(Alexa.getDownloader());
    }

    @Test
    public void testGetName() throws Exception {
        assertFalse(baker.getName().isEmpty());
    }

    @Test
    public void testId() throws Exception {
        assertEquals(baker.getId(), "Trending");
    }

    @Test
    public void testGetStreamsQuantity() throws Exception {
        ListBaker.InfoItemsPage<StreamInfoItem> page = baker.getInitialPage();
        Utils.printErrors(page.getErrors());
        assertTrue("no streams are received", page.getItems().size() >= 20);
    }

    @Test
    public void testGetStreamsErrors() throws Exception {
        assertEmptyErrors("errors during stream list extraction", baker.getInitialPage().getErrors());
    }

    @Test
    public void testHasMoreStreams() throws Exception {
        // Setup the streams
        baker.getInitialPage();
        assertFalse("has more streams", baker.hasNextPage());
    }

    @Test
    public void testGetNextPage() {
        assertTrue("baker has next streams", baker.getPage(baker.getNextPageUrl()) == null
                || baker.getPage(baker.getNextPageUrl()).getItems().isEmpty());
    }

    @Test
    public void testGetUrl() throws Exception {
        assertEquals(baker.getUrl(), baker.getUrl(), "https://www.youtube.com/feed/trending");
    }
}
