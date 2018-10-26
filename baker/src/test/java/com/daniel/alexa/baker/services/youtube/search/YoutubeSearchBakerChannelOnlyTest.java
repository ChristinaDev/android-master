package com.daniel.alexa.baker.services.youtube.search;

import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.channel.ChannelInfoItem;
import com.daniel.alexa.baker.services.youtube.bakers.YoutubeSearchBaker;
import com.daniel.alexa.baker.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static com.daniel.alexa.baker.ServiceList.YouTube;

public class YoutubeSearchBakerChannelOnlyTest extends YoutubeSearchBakerBaseTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        Alexa.init(Downloader.getInstance());
        baker = (YoutubeSearchBaker) YouTube.getSearchBaker("pewdiepie",
                asList(YoutubeSearchQueryHandlerFactory.CHANNELS), null, "de");
        baker.fetchPage();
        itemsPage = baker.getInitialPage();
    }

    @Test
    public void testGetSecondPage() throws Exception {
        YoutubeSearchBaker secondBaker = (YoutubeSearchBaker) YouTube.getSearchBaker("pewdiepie",
                asList(YoutubeSearchQueryHandlerFactory.CHANNELS), null, "de");
        ListBaker.InfoItemsPage<InfoItem> secondPage = secondBaker.getPage(itemsPage.getNextPageUrl());
        assertTrue(Integer.toString(secondPage.getItems().size()),
                secondPage.getItems().size() > 10);

   
        boolean equals = true;
        for (int i = 0; i < secondPage.getItems().size()
                && i < itemsPage.getItems().size(); i++) {
            if(!secondPage.getItems().get(i).getUrl().equals(
                    itemsPage.getItems().get(i).getUrl())) {
                equals = false;
            }
        }
        assertFalse("First and second page are equal", equals);

        assertEquals("https://www.youtube.com/results?q=pewdiepie&sp=EgIQAlAU&page=3", secondPage.getNextPageUrl());
    }

    @Test
    public void testGetSecondPageUrl() throws Exception {
        assertEquals("https://www.youtube.com/results?q=pewdiepie&sp=EgIQAlAU&page=2", baker.getNextPageUrl());
    }

    @Test
    public void testOnlyContainChannels() {
        for(InfoItem item : itemsPage.getItems()) {
            if(!(item instanceof ChannelInfoItem)) {
                fail("The following item is no channel item: " + item.toString());
            }
        }
    }
}
