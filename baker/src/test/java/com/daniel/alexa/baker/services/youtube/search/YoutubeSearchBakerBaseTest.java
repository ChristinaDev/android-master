package com.daniel.alexa.baker.services.youtube.search;

import org.junit.Test;
import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.services.BaseListBakerTest;
import com.daniel.alexa.baker.services.youtube.bakers.YoutubeSearchBaker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public abstract class YoutubeSearchBakerBaseTest {

    protected static YoutubeSearchBaker baker;
    protected static ListBaker.InfoItemsPage<InfoItem> itemsPage;


    @Test
    public void testResultListElementsLength() {
        assertTrue(Integer.toString(itemsPage.getItems().size()),
                itemsPage.getItems().size() > 10);
    }

    @Test
    public void testUrl() throws Exception {
        assertTrue(baker.getUrl(), baker.getUrl().startsWith("https://www.youtube.com"));
    }
}
