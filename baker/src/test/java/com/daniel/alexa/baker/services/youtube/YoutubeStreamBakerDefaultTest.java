package com.daniel.alexa.baker.services.youtube;

import org.junit.BeforeClass;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.services.youtube.bakers.YoutubeStreamBaker;
import com.daniel.alexa.baker.stream.*;
import com.daniel.alexa.baker.utils.DashMpdParser;
import com.daniel.alexa.baker.utils.Utils;

import java.io.IOException;

import static org.junit.Assert.*;
import static com.daniel.alexa.baker.BakerAsserts.assertIsSecureUrl;
import static com.daniel.alexa.baker.ServiceList.YouTube;
import static com.daniel.alexa.baker.services.youtube.YoutubeTrendingBakerTest.baker;

/**
 * Test for {@link StreamBaker}
 */
public class YoutubeStreamBakerDefaultTest {

    public static class AdeleHello {
        private static YoutubeStreamBaker baker;

        @BeforeClass
        public static void setUp() throws Exception {
            Alexa.init(Downloader.getInstance());
            baker = (YoutubeStreamBaker) YouTube
                    .getStreamBaker("https://www.youtube.com/watch?v=rYEDA3JcQqw");
            baker.fetchPage();
        }

        @Test
        public void testGetInvalidTimeStamp() throws ParsingException {
            assertTrue(baker.getTimeStamp() + "",
                    baker.getTimeStamp() <= 0);
        }

        @Test
        public void testGetValidTimeStamp() throws ExtractionException {
            StreamBaker baker = YouTube.getStreamBaker("https://youtu.be/FmG385_uUys?t=174");
            assertEquals(baker.getTimeStamp() + "", "174");
        }

        @Test
        public void testGetTitle() throws ParsingException {
            assertFalse(baker.getName().isEmpty());
        }

        @Test
        public void testGetDescription() throws ParsingException {
            assertNotNull(baker.getDescription());
            assertFalse(baker.getDescription().isEmpty());
        }

        @Test
        public void testGetFullLinksInDescriptlion() throws ParsingException {
            assertTrue(baker.getDescription().contains("http://smarturl.it/SubscribeAdele?IQid=yt"));
            assertFalse(baker.getDescription().contains("http://smarturl.it/SubscribeAdele?IQi..."));
        }

        @Test
        public void testGetUploaderName() throws ParsingException {
            assertNotNull(baker.getUploaderName());
            assertFalse(baker.getUploaderName().isEmpty());
        }


        @Test
        public void testGetLength() throws ParsingException {
            assertTrue(baker.getLength() > 0);
        }

        @Test
        public void testGetViewCount() throws ParsingException {
            Long count = baker.getViewCount();
            assertTrue(Long.toString(count), count >= /* specific to that video */ 1220025784);
        }

        @Test
        public void testGetUploadDate() throws ParsingException {
            assertTrue(baker.getUploadDate().length() > 0);
        }

        @Test
        public void testGetUploaderUrl() throws ParsingException {
            assertTrue(baker.getUploaderUrl().length() > 0);
        }

        @Test
        public void testGetThumbnailUrl() throws ParsingException {
            assertIsSecureUrl(baker.getThumbnailUrl());
        }

        @Test
        public void testGetUploaderAvatarUrl() throws ParsingException {
            assertIsSecureUrl(baker.getUploaderAvatarUrl());
        }

        @Test
        public void testGetAudioStreams() throws IOException, ExtractionException {
            assertFalse(baker.getAudioStreams().isEmpty());
        }

        @Test
        public void testGetVideoStreams() throws IOException, ExtractionException {
            for (VideoStream s : baker.getVideoStreams()) {
                assertIsSecureUrl(s.url);
                assertTrue(s.resolution.length() > 0);
                assertTrue(Integer.toString(s.getFormatId()),
                        0 <= s.getFormatId() && s.getFormatId() <= 4);
            }
        }

        @Test
        public void testStreamType() throws ParsingException {
            assertTrue(baker.getStreamType() == StreamType.VIDEO_STREAM);
        }

        @Test
        public void testGetDashMpd() throws ParsingException {
      
            assertTrue(baker.getDashMpdUrl(),
                    baker.getDashMpdUrl() != null && baker.getDashMpdUrl().isEmpty());
        }

        @Test
        public void testGetRelatedVideos() throws ExtractionException, IOException {
            StreamInfoItemsCollector relatedVideos = baker.getRelatedVideos();
            Utils.printErrors(relatedVideos.getErrors());
            assertFalse(relatedVideos.getItems().isEmpty());
            assertTrue(relatedVideos.getErrors().isEmpty());
        }

        @Test
        public void testGetSubtitlesListDefault() throws IOException, ExtractionException {
       
            assertTrue(baker.getSubtitlesDefault().isEmpty());
        }

        @Test
        public void testGetSubtitlesList() throws IOException, ExtractionException {
       
            assertTrue(baker.getSubtitles(SubtitlesFormat.TTML).isEmpty());
        }
    }

    public static class DescriptionTestPewdiepie {
        private static YoutubeStreamBaker baker;

        @BeforeClass
        public static void setUp() throws Exception {
            Alexa.init(Downloader.getInstance());
            baker = (YoutubeStreamBaker) YouTube
                    .getStreamBaker("https://www.youtube.com/watch?v=dJY8iT341F4");
            baker.fetchPage();
        }

        @Test
        public void testGetDescription() throws ParsingException {
            assertNotNull(baker.getDescription());
            assertFalse(baker.getDescription().isEmpty());
        }

        @Test
        public void testGetFullLinksInDescriptlion() throws ParsingException {
            assertTrue(baker.getDescription().contains("https://www.reddit.com/r/PewdiepieSubmissions/"));
            assertTrue(baker.getDescription().contains("https://www.youtube.com/channel/UC3e8EMTOn4g6ZSKggHTnNng"));

            assertFalse(baker.getDescription().contains("https://www.reddit.com/r/PewdiepieSub..."));
            assertFalse(baker.getDescription().contains("https://usa.clutchchairz.com/product/..."));
            assertFalse(baker.getDescription().contains("https://europe.clutchchairz.com/en/pr..."));
            assertFalse(baker.getDescription().contains("https://canada.clutchchairz.com/produ..."));
            assertFalse(baker.getDescription().contains("http://store.steampowered.com/app/703..."));
            assertFalse(baker.getDescription().contains("https://www.youtube.com/channel/UC3e8..."));
        }
    }
}
