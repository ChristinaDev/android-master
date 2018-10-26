package com.daniel.alexa.baker.services.youtube.search;

import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.channel.ChannelInfoItem;
import com.daniel.alexa.baker.services.youtube.bakers.YoutubeSearchBaker;
import com.daniel.alexa.baker.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory;

import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertTrue;
import static com.daniel.alexa.baker.ServiceList.YouTube;

public class YoutubeSearchCountTest {
    public static class YoutubeChannelViewCountTest extends YoutubeSearchBakerBaseTest {
        @BeforeClass
        public static void setUpClass() throws Exception {
            Alexa.init(Downloader.getInstance());
            baker = (YoutubeSearchBaker) YouTube.getSearchBaker("pewdiepie",
                    singletonList(YoutubeSearchQueryHandlerFactory.CHANNELS), null,"de");
            baker.fetchPage();
            itemsPage = baker.getInitialPage();
        }

        @Test
        public void testViewCount() throws Exception {
            boolean foundKnownChannel = false;
            ChannelInfoItem ci = (ChannelInfoItem) itemsPage.getItems().get(0);
            assertTrue("Count does not fit: " + Long.toString(ci.getSubscriberCount()),
                    65043316 < ci.getSubscriberCount() && ci.getSubscriberCount() < 68043316);
        }
    }
}
