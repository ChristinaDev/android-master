package com.daniel.alexa.baker.services.youtube;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import com.daniel.alexa.Downloader;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.services.youtube.bakers.YoutubeStreamBaker;
import com.daniel.alexa.baker.services.youtube.linkHandler.YoutubeStreamLinkHandlerFactory;
import com.daniel.alexa.baker.stream.StreamBaker;
import com.daniel.alexa.baker.stream.SubtitlesFormat;
import com.daniel.alexa.baker.stream.VideoStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static com.daniel.alexa.baker.BakerAsserts.assertIsSecureUrl;
import static com.daniel.alexa.baker.ServiceList.YouTube;

/**
 * Test for {@link YoutubeStreamLinkHandlerFactory}
 */
public class YoutubeStreamBakerAgeRestrictedTest {
    public static final String HTTPS = "https://";
    private static YoutubeStreamBaker baker;

    @BeforeClass
    public static void setUp() throws Exception {
        Alexa.init(Downloader.getInstance());
        baker = (YoutubeStreamBaker) YouTube
                .getStreamBaker("https://www.youtube.com/watch?v=MmBeUZqv1QA");
        baker.fetchPage();
    }

    @Test
    public void testGetInvalidTimeStamp() throws ParsingException {
        assertTrue(baker.getTimeStamp() + "", baker.getTimeStamp() <= 0);
    }

    @Test
    public void testGetValidTimeStamp() throws IOException, ExtractionException {
        StreamBaker baker = YouTube.getStreamBaker("https://youtu.be/FmG385_uUys?t=174");
        assertEquals(baker.getTimeStamp() + "", "174");
    }

    @Test
    public void testGetAgeLimit() throws ParsingException {
        assertEquals(18, baker.getAgeLimit());
    }

    @Test
    public void testGetName() throws ParsingException {
        assertNotNull("name is null", baker.getName());
        assertFalse("name is empty", baker.getName().isEmpty());
    }

    @Test
    public void testGetDescription() throws ParsingException {
        assertNotNull(baker.getDescription());
        assertFalse(baker.getDescription().isEmpty());
    }

    @Test
    public void testGetUploaderName() throws ParsingException {
        assertNotNull(baker.getUploaderName());
        assertFalse(baker.getUploaderName().isEmpty());
    }

    @Ignore // Currently there is no way get the length from restricted videos
    @Test
    public void testGetLength() throws ParsingException {
        assertTrue(baker.getLength() > 0);
    }

    @Test
    public void testGetViews() throws ParsingException {
        assertTrue(baker.getViewCount() > 0);
    }

    @Test
    public void testGetUploadDate() throws ParsingException {
        assertTrue(baker.getUploadDate().length() > 0);
    }

    @Test
    public void testGetThumbnailUrl() throws ParsingException {
        assertIsSecureUrl(baker.getThumbnailUrl());
    }

    @Test
    public void testGetUploaderAvatarUrl() throws ParsingException {
        assertIsSecureUrl(baker.getUploaderAvatarUrl());
    }

    // FIXME: 25.11.17 Are there no streams or are they not listed?
    @Ignore
    @Test
    public void testGetAudioStreams() throws IOException, ExtractionException {
        // audio streams are not always necessary
        assertFalse(baker.getAudioStreams().isEmpty());
    }

    @Test
    public void testGetVideoStreams() throws IOException, ExtractionException {
        List<VideoStream> streams = new ArrayList<>();
        streams.addAll(baker.getVideoStreams());
        streams.addAll(baker.getVideoOnlyStreams());

        assertTrue(Integer.toString(streams.size()),streams.size() > 0);
        for (VideoStream s : streams) {
            assertTrue(s.getUrl(),
                    s.getUrl().contains(HTTPS));
            assertTrue(s.resolution.length() > 0);
            assertTrue(Integer.toString(s.getFormatId()),
                    0 <= s.getFormatId() && s.getFormatId() <= 4);
        }
    }


    @Test
    public void testGetSubtitlesListDefault() throws IOException, ExtractionException {
        // Video (/view?v=YQHsXMglC9A) set in the setUp() method has no captions => null
        assertTrue(baker.getSubtitlesDefault().isEmpty());
    }

    @Test
    public void testGetSubtitlesList() throws IOException, ExtractionException {
        // Video (/view?v=YQHsXMglC9A) set in the setUp() method has no captions => null
        assertTrue(baker.getSubtitles(SubtitlesFormat.TTML).isEmpty());
    }
}
