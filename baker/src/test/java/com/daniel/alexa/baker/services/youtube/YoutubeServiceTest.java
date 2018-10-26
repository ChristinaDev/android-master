package com.daniel.alexa.baker.services.youtube;


import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.kiosk.KioskList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static com.daniel.alexa.baker.ServiceList.YouTube;

/**
 * Test for {@link YoutubeService}
 */
public class YoutubeServiceTest {
    static StreamingService service;
    static KioskList kioskList;

    @BeforeClass
    public static void setUp() throws Exception {
        Alexa.init(Downloader.getInstance());
        service = YouTube;
        kioskList = service.getKioskList();
    }

    @Test
    public void testGetKioskAvailableKiosks() throws Exception {
        assertFalse("No kiosk got returned", kioskList.getAvailableKiosks().isEmpty());
    }

    @Test
    public void testGetDefaultKiosk() throws Exception {
        assertEquals(kioskList.getDefaultKioskBaker(null).getId(), "Trending");
    }
}
