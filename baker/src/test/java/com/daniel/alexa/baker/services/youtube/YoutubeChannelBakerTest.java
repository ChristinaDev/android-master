package com.daniel.alexa.baker.services.youtube;

import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.ServiceList;
import com.daniel.alexa.baker.channel.ChannelBaker;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.services.BaseChannelBakerTest;
import com.daniel.alexa.baker.services.youtube.bakers.YoutubeChannelBaker;

import static org.junit.Assert.*;
import static com.daniel.alexa.baker.BakerAsserts.assertIsSecureUrl;
import static com.daniel.alexa.baker.ServiceList.YouTube;
import static com.daniel.alexa.baker.services.DefaultTests.*;

/**
 * Test for {@link ChannelBaker}
 */
public class YoutubeChannelBakerTest {
    public static class Gronkh implements BaseChannelBakerTest {
        private static YoutubeChannelBaker baker;

        @BeforeClass
        public static void setUp() throws Exception {
            Alexa.init(Downloader.getInstance());
            baker = (YoutubeChannelBaker) YouTube
                    .getChannelBaker("http://www.youtube.com/user/Gronkh");
            baker.fetchPage();
        }

    
        @Test
        public void testServiceId() {
            assertEquals(YouTube.getServiceId(), baker.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals("Gronkh", baker.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals("UCYJ61XIK64sp6ZFFS8sctxw", baker.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCYJ61XIK64sp6ZFFS8sctxw", baker.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("http://www.youtube.com/user/Gronkh", baker.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListBaker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testRelatedItems() throws Exception {
            defaultTestRelatedItems(baker, YouTube.getServiceId());
        }

        @Test
        public void testMoreRelatedItems() throws Exception {
            defaultTestMoreItems(baker, ServiceList.YouTube.getServiceId());
        }

         /*//////////////////////////////////////////////////////////////////////////
         // ChannelBaker
         //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            assertTrue(baker.getDescription().contains("Zart im Schmelz und süffig im Abgang. Ungebremster Spieltrieb"));
        }

        @Test
        public void testAvatarUrl() throws Exception {
            String avatarUrl = baker.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl, avatarUrl.contains("yt3"));
        }

        @Test
        public void testBannerUrl() throws Exception {
            String bannerUrl = baker.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt3"));
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertEquals("https://www.youtube.com/feeds/videos.xml?channel_id=UCYJ61XIK64sp6ZFFS8sctxw", baker.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            assertTrue("Wrong subscriber count", baker.getSubscriberCount() >= 0);
        }

        @Test
        public void testChannelDonation() throws Exception {
        
            assertTrue(baker.getDonationLinks().length == 0);
        }
    }


    public static class VSauce implements BaseChannelBakerTest {
        private static YoutubeChannelBaker baker;

        @BeforeClass
        public static void setUp() throws Exception {
            Alexa.init(Downloader.getInstance());
            baker = (YoutubeChannelBaker) YouTube
                    .getChannelBaker("https://www.youtube.com/user/Vsauce");
            baker.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // baker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(YouTube.getServiceId(), baker.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals("Vsauce", baker.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals("UC6nSFpj9HTCZ5t-N3Rm3-HA", baker.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UC6nSFpj9HTCZ5t-N3Rm3-HA", baker.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/user/Vsauce", baker.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListBaker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testRelatedItems() throws Exception {
            defaultTestRelatedItems(baker, YouTube.getServiceId());
        }

        @Test
        public void testMoreRelatedItems() throws Exception {
            defaultTestMoreItems(baker, ServiceList.YouTube.getServiceId());
        }

         /*//////////////////////////////////////////////////////////////////////////
         // ChannelBaker
         //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            assertTrue("What it actually was: " + baker.getDescription(),
                    baker.getDescription().contains("Our World is Amazing. Questions? Ideas? Tweet me:"));
        }

        @Test
        public void testAvatarUrl() throws Exception {
            String avatarUrl = baker.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl, avatarUrl.contains("yt3"));
        }

        @Test
        public void testBannerUrl() throws Exception {
            String bannerUrl = baker.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt3"));
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertEquals("https://www.youtube.com/feeds/videos.xml?channel_id=UC6nSFpj9HTCZ5t-N3Rm3-HA", baker.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            assertTrue("Wrong subscriber count", baker.getSubscriberCount() >= 0);
        }

        @Test
        public void testChannelDonation() throws Exception {
           
            assertTrue(baker.getDonationLinks().length == 0);
        }
    }

    public static class Kurzgesagt implements BaseChannelBakerTest {
        private static YoutubeChannelBaker baker;

        @BeforeClass
        public static void setUp() throws Exception {
            Alexa.init(Downloader.getInstance());
            baker = (YoutubeChannelBaker) YouTube
                    .getChannelBaker("https://www.youtube.com/channel/UCsXVk37bltHxD1rDPwtNM8Q");
            baker.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // Additional Testing
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testGetPageInNewBaker() throws Exception {
            final ChannelBaker newBaker = YouTube.getChannelBaker(baker.getUrl());
            defaultTestGetPageInNewBaker(baker, newBaker, YouTube.getServiceId());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // baker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(YouTube.getServiceId(), baker.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            String name = baker.getName();
            assertTrue(name, name.startsWith("Kurzgesagt"));
        }

        @Test
        public void testId() throws Exception {
            assertEquals("UCsXVk37bltHxD1rDPwtNM8Q", baker.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCsXVk37bltHxD1rDPwtNM8Q", baker.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCsXVk37bltHxD1rDPwtNM8Q", baker.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListBaker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testRelatedItems() throws Exception {
            defaultTestRelatedItems(baker, YouTube.getServiceId());
        }

        @Test
        public void testMoreRelatedItems() throws Exception {
            defaultTestMoreItems(baker, ServiceList.YouTube.getServiceId());
        }

         /*//////////////////////////////////////////////////////////////////////////
         // ChannelBaker
         //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            final String description = baker.getDescription();
            assertTrue(description, description.contains("small team who want to make science look beautiful"));
       
        }

        @Test
        public void testAvatarUrl() throws Exception {
            String avatarUrl = baker.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl, avatarUrl.contains("yt3"));
        }

        @Test
        public void testBannerUrl() throws Exception {
            String bannerUrl = baker.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt3"));
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertEquals("https://www.youtube.com/feeds/videos.xml?channel_id=UCsXVk37bltHxD1rDPwtNM8Q", baker.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            assertTrue("Wrong subscriber count", baker.getSubscriberCount() >= 5e6);
        }

        @Test
        public void testChannelDonation() throws Exception {
            assertTrue(baker.getDonationLinks().length == 1);
        }
    }

    public static class CaptainDisillusion implements BaseChannelBakerTest {
        private static YoutubeChannelBaker baker;

        @BeforeClass
        public static void setUp() throws Exception {
            Alexa.init(Downloader.getInstance());
            baker = (YoutubeChannelBaker) YouTube
                    .getChannelBaker("https://www.youtube.com/user/CaptainDisillusion/videos");
            baker.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // baker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(YouTube.getServiceId(), baker.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals("CaptainDisillusion", baker.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals("UCEOXxzW2vU0P-0THehuIIeg", baker.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCEOXxzW2vU0P-0THehuIIeg", baker.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/user/CaptainDisillusion/videos", baker.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListBaker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testRelatedItems() throws Exception {
            defaultTestRelatedItems(baker, YouTube.getServiceId());
        }

        @Test
        public void testMoreRelatedItems() throws Exception {
            defaultTestMoreItems(baker, ServiceList.YouTube.getServiceId());
        }

         /*//////////////////////////////////////////////////////////////////////////
         // ChannelBaker
         //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            final String description = baker.getDescription();
            assertTrue(description, description.contains("In a world where"));
        }

        @Test
        public void testAvatarUrl() throws Exception {
            String avatarUrl = baker.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl, avatarUrl.contains("yt3"));
        }

        @Test
        public void testBannerUrl() throws Exception {
            String bannerUrl = baker.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt3"));
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertEquals("https://www.youtube.com/feeds/videos.xml?channel_id=UCEOXxzW2vU0P-0THehuIIeg", baker.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            assertTrue("Wrong subscriber count", baker.getSubscriberCount() >= 5e5);
        }
    }

    public static class RandomChannel implements BaseChannelBakerTest {
        private static YoutubeChannelBaker baker;

        @BeforeClass
        public static void setUp() throws Exception {
            Alexa.init(Downloader.getInstance());
            baker = (YoutubeChannelBaker) YouTube
                    .getChannelBaker("https://www.youtube.com/channel/UCUaQMQS9lY5lit3vurpXQ6w");
            baker.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // baker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(YouTube.getServiceId(), baker.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals("random channel", baker.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals("UCUaQMQS9lY5lit3vurpXQ6w", baker.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCUaQMQS9lY5lit3vurpXQ6w", baker.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCUaQMQS9lY5lit3vurpXQ6w", baker.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListBaker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testRelatedItems() throws Exception {
            defaultTestRelatedItems(baker, YouTube.getServiceId());
        }

        @Test
        public void testMoreRelatedItems() {
            try {
                defaultTestMoreItems(baker, YouTube.getServiceId());
            } catch (Throwable ignored) {
                return;
            }

            fail("This channel doesn't have more items, it should throw an error");
        }

         /*//////////////////////////////////////////////////////////////////////////
         // ChannelBaker
         //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            final String description = baker.getDescription();
            assertTrue(description, description.contains("Hey there iu will upoload a load of pranks onto this channel"));
        }

        @Test
        public void testAvatarUrl() throws Exception {
            String avatarUrl = baker.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl, avatarUrl.contains("yt3"));
        }

        @Test
        public void testBannerUrl() throws Exception {
            String bannerUrl = baker.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt3"));
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertEquals("https://www.youtube.com/feeds/videos.xml?channel_id=UCUaQMQS9lY5lit3vurpXQ6w", baker.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            assertTrue("Wrong subscriber count", baker.getSubscriberCount() >= 50);
        }

        @Test
        public void testChannelDonation() throws Exception {
            assertTrue(baker.getDonationLinks().length == 0);
        }
    }
};

