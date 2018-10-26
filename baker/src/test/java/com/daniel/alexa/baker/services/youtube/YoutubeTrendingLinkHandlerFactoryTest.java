package com.daniel.alexa.baker.services.youtube;

import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.linkhandler.LinkHandlerFactory;
import com.daniel.alexa.baker.services.youtube.linkHandler.YoutubeTrendingLinkHandlerFactory;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static com.daniel.alexa.baker.ServiceList.YouTube;

/**
 * Test for {@link YoutubeTrendingLinkHandlerFactory}
 */
public class YoutubeTrendingLinkHandlerFactoryTest {
    private static LinkHandlerFactory LinkHandlerFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        LinkHandlerFactory = YouTube.getKioskList().getListLinkHandlerFactoryByType("Trending");
        Alexa.init(Downloader.getInstance());
    }

    @Test
    public void getUrl()
            throws Exception {
        assertEquals(LinkHandlerFactory.fromId("").getUrl(), "https://www.youtube.com/feed/trending");
    }

    @Test
    public void getId()
            throws Exception {
        assertEquals(LinkHandlerFactory.fromUrl("https://www.youtube.com/feed/trending").getId(), "Trending");
    }

    @Test
    public void acceptUrl() throws ParsingException {
        assertTrue(LinkHandlerFactory.acceptUrl("https://www.youtube.com/feed/trending"));
        assertTrue(LinkHandlerFactory.acceptUrl("https://www.youtube.com/feed/trending?adsf=fjaj#fhe"));
        assertTrue(LinkHandlerFactory.acceptUrl("http://www.youtube.com/feed/trending"));
        assertTrue(LinkHandlerFactory.acceptUrl("www.youtube.com/feed/trending"));
        assertTrue(LinkHandlerFactory.acceptUrl("youtube.com/feed/trending"));
        assertTrue(LinkHandlerFactory.acceptUrl("youtube.com/feed/trending?akdsakjf=dfije&kfj=dkjak"));
        assertTrue(LinkHandlerFactory.acceptUrl("https://youtube.com/feed/trending"));
        assertTrue(LinkHandlerFactory.acceptUrl("m.youtube.com/feed/trending"));

        assertFalse(LinkHandlerFactory.acceptUrl("https://youtu.be/feed/trending"));
        assertFalse(LinkHandlerFactory.acceptUrl("kdskjfiiejfia"));
        assertFalse(LinkHandlerFactory.acceptUrl("https://www.youtube.com/bullshit/feed/trending"));
        assertFalse(LinkHandlerFactory.acceptUrl("https://www.youtube.com/feed/trending/bullshit"));
        assertFalse(LinkHandlerFactory.acceptUrl("https://www.youtube.com/feed/bullshit/trending"));
        assertFalse(LinkHandlerFactory.acceptUrl("peter klaut aepferl youtube.com/feed/trending"));
        assertFalse(LinkHandlerFactory.acceptUrl("youtube.com/feed/trending askjkf"));
        assertFalse(LinkHandlerFactory.acceptUrl("askdjfi youtube.com/feed/trending askjkf"));
        assertFalse(LinkHandlerFactory.acceptUrl("    youtube.com/feed/trending"));
        assertFalse(LinkHandlerFactory.acceptUrl("https://www.youtube.com/feed/trending.html"));
        assertFalse(LinkHandlerFactory.acceptUrl(""));
    }
}
