package com.daniel.alexa.baker.services.youtube.search;

import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.channel.ChannelInfoItem;
import com.daniel.alexa.baker.services.youtube.bakers.YoutubeSearchBaker;
import com.daniel.alexa.baker.stream.StreamInfoItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.daniel.alexa.baker.ServiceList.YouTube;

public class YoutubeSearchBakerDefaultTest extends YoutubeSearchBakerBaseTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        Alexa.init(Downloader.getInstance());
        baker = (YoutubeSearchBaker) YouTube.getSearchBaker("pewdiepie", "de");
        baker.fetchPage();
        itemsPage = baker.getInitialPage();
    }



    @Test
    public void testGetSecondPageUrl() throws Exception {
        assertEquals("https://www.youtube.com/results?q=pewdiepie&page=2", baker.getNextPageUrl());
    }

    @Test
    public void testResultList_FirstElement() {
        InfoItem firstInfoItem = itemsPage.getItems().get(0);

        assertTrue(firstInfoItem instanceof ChannelInfoItem);
        assertEquals("name", "PewDiePie", firstInfoItem.getName());
        assertEquals("url","https://www.youtube.com/user/PewDiePie", firstInfoItem.getUrl());
    }

    @Test
    public void testResultListCheckIfContainsStreamItems() {
        boolean hasStreams = false;
        for(InfoItem item : itemsPage.getItems()) {
            if(item instanceof StreamInfoItem) {
                hasStreams = true;
            }
        }
        assertTrue("Has no InfoItemStreams", hasStreams);
    }

    @Test
    public void testGetSecondPage() throws Exception {
        YoutubeSearchBaker secondBaker =
                (YoutubeSearchBaker) YouTube.getSearchBaker("pewdiepie", "de");
        ListBaker.InfoItemsPage<InfoItem> secondPage = secondBaker.getPage(itemsPage.getNextPageUrl());
        assertTrue(Integer.toString(secondPage.getItems().size()),
                secondPage.getItems().size() > 10);

        // check if its the same result
        boolean equals = true;
        for (int i = 0; i < secondPage.getItems().size()
                && i < itemsPage.getItems().size(); i++) {
            if(!secondPage.getItems().get(i).getUrl().equals(
                    itemsPage.getItems().get(i).getUrl())) {
                equals = false;
            }
        }
        assertFalse("First and second page are equal", equals);

        assertEquals("https://www.youtube.com/results?q=pewdiepie&page=3", secondPage.getNextPageUrl());
    }

    @Test
    public void testSuggestionNotNull() throws Exception {
        //todo write a real test
        assertTrue(baker.getSearchSuggestion() != null);
    }


    @Test
    public void testId() throws Exception {
        assertEquals("pewdiepie", baker.getId());
    }

    @Test
    public void testName() {
        assertEquals("pewdiepie", baker.getName());
    }
}
