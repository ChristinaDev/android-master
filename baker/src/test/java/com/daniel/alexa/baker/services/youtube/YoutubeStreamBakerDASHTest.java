package com.daniel.alexa.baker.services.youtube;


import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.stream.StreamBaker;
import com.daniel.alexa.baker.stream.StreamInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static com.daniel.alexa.baker.ServiceList.YouTube;

/**
 * Test for {@link StreamBaker}
 */
public class YoutubeStreamBakerDASHTest {
    private static StreamInfo info;

    @BeforeClass
    public static void setUp() throws Exception {
        Alexa.init(Downloader.getInstance());
        info = StreamInfo.getInfo(YouTube, "https://www.youtube.com/watch?v=00Q4SUnVQK4");
    }

    @Test
    public void testGetDashMpd() {
        System.out.println(info.getDashMpdUrl());
        assertTrue(info.getDashMpdUrl(),
                info.getDashMpdUrl() != null && !info.getDashMpdUrl().isEmpty());
    }

    @Test
    public void testDashMpdParser() {
        assertEquals(0, info.getAudioStreams().size());
        assertEquals(0, info.getVideoOnlyStreams().size());
        assertEquals(4, info.getVideoStreams().size());
    }
}
