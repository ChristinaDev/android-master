package com.daniel.alexa.baker.services.youtube;


import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.linkhandler.LinkHandlerFactory;
import com.daniel.alexa.baker.kiosk.KioskInfo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.daniel.alexa.baker.ServiceList.YouTube;

/**
 * Test for {@link KioskInfo}
 */
public class YoutubeTrendingKioskInfoTest {
    static KioskInfo kioskInfo;

    @BeforeClass
    public static void setUp()
            throws Exception {
        Alexa.init(Downloader.getInstance());
        StreamingService service = YouTube;
        LinkHandlerFactory LinkHandlerFactory = service.getKioskList().getListLinkHandlerFactoryByType("Trending");

        kioskInfo = KioskInfo.getInfo(YouTube, LinkHandlerFactory.fromId("Trending").getUrl(), null);
    }

    @Test
    public void getStreams() {
        assertFalse(kioskInfo.getRelatedItems().isEmpty());
    }

    @Test
    public void getId() {
        assertTrue(kioskInfo.getId().equals("Trending")
                || kioskInfo.getId().equals("Trends"));
    }

    @Test
    public void getName() {
        assertFalse(kioskInfo.getName().isEmpty());
    }
}
