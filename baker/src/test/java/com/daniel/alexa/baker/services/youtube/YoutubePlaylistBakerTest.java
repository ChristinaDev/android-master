package com.daniel.alexa.baker.services.youtube;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.ServiceList;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.playlist.PlaylistBaker;
import com.daniel.alexa.baker.services.BasePlaylistBakerTest;
import com.daniel.alexa.baker.services.youtube.bakers.YoutubePlaylistBaker;
import com.daniel.alexa.baker.stream.StreamInfoItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static com.daniel.alexa.baker.BakerAsserts.assertIsSecureUrl;
import static com.daniel.alexa.baker.ServiceList.YouTube;
import static com.daniel.alexa.baker.services.DefaultTests.*;

/**
 * Test for {@link YoutubePlaylistBaker}
 */
public class YoutubePlaylistBakerTest {
    public static class TimelessPopHits implements BasePlaylistBakerTest {
        private static YoutubePlaylistBaker baker;

        @BeforeClass
        public static void setUp() throws Exception {
            Alexa.init(Downloader.getInstance());
            baker = (YoutubePlaylistBaker) YouTube
                    .getPlaylistBaker("http://www.youtube.com/watch?v=lp-EO5I60KA&list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj");
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
            String name = baker.getName();
            assertTrue(name, name.startsWith("Pop Music Playlist"));
        }

        @Test
        public void testId() throws Exception {
            assertEquals("PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj", baker.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj", baker.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("http://www.youtube.com/watch?v=lp-EO5I60KA&list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj", baker.getOriginalUrl());
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
        // PlaylistBaker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testThumbnailUrl() throws Exception {
            final String thumbnailUrl = baker.getThumbnailUrl();
            assertIsSecureUrl(thumbnailUrl);
            assertTrue(thumbnailUrl, thumbnailUrl.contains("yt"));
        }

        @Ignore
        @Test
        public void testBannerUrl() throws Exception {
            final String bannerUrl = baker.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt"));
        }

        @Test
        public void testUploaderUrl() throws Exception {
            assertTrue(baker.getUploaderUrl().contains("youtube.com"));
        }

        @Test
        public void testUploaderName() throws Exception {
            final String uploaderName = baker.getUploaderName();
            assertTrue(uploaderName, uploaderName.contains("Just Hits"));
        }

        @Test
        public void testUploaderAvatarUrl() throws Exception {
            final String uploaderAvatarUrl = baker.getUploaderAvatarUrl();
            assertTrue(uploaderAvatarUrl, uploaderAvatarUrl.contains("yt"));
        }

        @Test
        public void testStreamCount() throws Exception {
            assertTrue("Error in the streams count", baker.getStreamCount() > 100);
        }
    }

    public static class HugePlaylist implements BasePlaylistBakerTest {
        private static YoutubePlaylistBaker baker;

        @BeforeClass
        public static void setUp() throws Exception {
            Alexa.init(Downloader.getInstance());
            baker = (YoutubePlaylistBaker) YouTube
                    .getPlaylistBaker("https://www.youtube.com/watch?v=8SbUC-UaAxE&list=PLWwAypAcFRgKAIIFqBr9oy-ZYZnixa_Fj");
            baker.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // Additional Testing
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testGetPageInNewBaker() throws Exception {
            final PlaylistBaker newBaker = YouTube.getPlaylistBaker(baker.getUrl());
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
            final String name = baker.getName();
            assertEquals("I Wanna Rock Super Gigantic Playlist 1: Hardrock, AOR, Metal and more !!! 5000 music videos !!!", name);
        }

        @Test
        public void testId() throws Exception {
            assertEquals("PLWwAypAcFRgKAIIFqBr9oy-ZYZnixa_Fj", baker.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/playlist?list=PLWwAypAcFRgKAIIFqBr9oy-ZYZnixa_Fj", baker.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/watch?v=8SbUC-UaAxE&list=PLWwAypAcFRgKAIIFqBr9oy-ZYZnixa_Fj", baker.getOriginalUrl());
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
            ListBaker.InfoItemsPage<StreamInfoItem> currentPage
                    = defaultTestMoreItems(baker, ServiceList.YouTube.getServiceId());
            // Test for 2 more levels

            for (int i = 0; i < 2; i++) {
                currentPage = baker.getPage(currentPage.getNextPageUrl());
                defaultTestListOfItems(YouTube.getServiceId(), currentPage.getItems(), currentPage.getErrors());
            }
        }

        /*//////////////////////////////////////////////////////////////////////////
        // PlaylistBaker
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testThumbnailUrl() throws Exception {
            final String thumbnailUrl = baker.getThumbnailUrl();
            assertIsSecureUrl(thumbnailUrl);
            assertTrue(thumbnailUrl, thumbnailUrl.contains("yt"));
        }

        @Ignore
        @Test
        public void testBannerUrl() throws Exception {
            final String bannerUrl = baker.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt"));
        }

        @Test
        public void testUploaderUrl() throws Exception {
            assertTrue(baker.getUploaderUrl().contains("youtube.com"));
        }

        @Test
        public void testUploaderName() throws Exception {
            assertEquals("Tomas Nilsson", baker.getUploaderName());
        }

        @Test
        public void testUploaderAvatarUrl() throws Exception {
            final String uploaderAvatarUrl = baker.getUploaderAvatarUrl();
            assertTrue(uploaderAvatarUrl, uploaderAvatarUrl.contains("yt"));
        }

        @Test
        public void testStreamCount() throws Exception {
            assertTrue("Error in the streams count", baker.getStreamCount() > 100);
        }
    }
}
