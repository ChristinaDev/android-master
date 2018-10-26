package com.daniel.alexa.baker.utils;

import com.daniel.alexa.baker.Downloader;
import com.daniel.alexa.baker.MediaFormat;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.exceptions.ReCaptchaException;
import com.daniel.alexa.baker.services.youtube.ItagItem;
import com.daniel.alexa.baker.stream.AudioStream;
import com.daniel.alexa.baker.stream.Stream;
import com.daniel.alexa.baker.stream.StreamInfo;
import com.daniel.alexa.baker.stream.VideoStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DashMpdParser {

    private DashMpdParser() {
    }

    public static class DashMpdParsingException extends ParsingException {
        DashMpdParsingException(String message, Exception e) {
            super(message, e);
        }
    }

    public static class ParserResult {
        private final List<VideoStream> videoStreams;
        private final List<AudioStream> audioStreams;
        private final List<VideoStream> videoOnlyStreams;

        public ParserResult(List<VideoStream> videoStreams, List<AudioStream> audioStreams, List<VideoStream> videoOnlyStreams) {
            this.videoStreams = videoStreams;
            this.audioStreams = audioStreams;
            this.videoOnlyStreams = videoOnlyStreams;
        }

        public List<VideoStream> getVideoStreams() {
            return videoStreams;
        }

        public List<AudioStream> getAudioStreams() {
            return audioStreams;
        }

        public List<VideoStream> getVideoOnlyStreams() {
            return videoOnlyStreams;
        }
    }

   
    public static ParserResult getStreams(final StreamInfo streamInfo) throws DashMpdParsingException, ReCaptchaException {
        String dashDoc;
        Downloader downloader = Alexa.getDownloader();
        try {
            dashDoc = downloader.download(streamInfo.getDashMpdUrl());
        } catch (IOException ioe) {
            throw new DashMpdParsingException("Could not get dash mpd: " + streamInfo.getDashMpdUrl(), ioe);
        } catch (ReCaptchaException e) {
            throw new ReCaptchaException("reCaptcha Challenge needed");
        }

        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final InputStream stream = new ByteArrayInputStream(dashDoc.getBytes());

            final Document doc = builder.parse(stream);
            final NodeList representationList = doc.getElementsByTagName("Representation");

            final List<VideoStream> videoStreams = new ArrayList<>();
            final List<AudioStream> audioStreams = new ArrayList<>();
            final List<VideoStream> videoOnlyStreams = new ArrayList<>();

            for (int i = 0; i < representationList.getLength(); i++) {
                final Element representation = (Element) representationList.item(i);
                try {
                    final String mimeType = ((Element) representation.getParentNode()).getAttribute("mimeType");
                    final String id = representation.getAttribute("id");
                    final String url = representation.getElementsByTagName("BaseURL").item(0).getTextContent();
                    final ItagItem itag = ItagItem.getItag(Integer.parseInt(id));
                    final Node segmentationList = representation.getElementsByTagName("SegmentList").item(0);

               
                    if (itag != null && segmentationList == null) {
                        final MediaFormat mediaFormat = MediaFormat.getFromMimeType(mimeType);

                        if (itag.itagType.equals(ItagItem.ItagType.AUDIO)) {
                            final AudioStream audioStream = new AudioStream(url, mediaFormat, itag.avgBitrate);

                            if (!Stream.containSimilarStream(audioStream, streamInfo.getAudioStreams())) {
                                audioStreams.add(audioStream);
                            }
                        } else {
                            boolean isVideoOnly = itag.itagType.equals(ItagItem.ItagType.VIDEO_ONLY);
                            final VideoStream videoStream = new VideoStream(url, mediaFormat, itag.resolutionString, isVideoOnly);

                            if (isVideoOnly) {
                                if (!Stream.containSimilarStream(videoStream, streamInfo.getVideoOnlyStreams())) {
                                    streamInfo.getVideoOnlyStreams().add(videoStream);
                                    videoOnlyStreams.add(videoStream);
                                }
                            } else if (!Stream.containSimilarStream(videoStream, streamInfo.getVideoStreams())) {
                                videoStreams.add(videoStream);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            return new ParserResult(videoStreams, audioStreams, videoOnlyStreams);
        } catch (Exception e) {
            throw new DashMpdParsingException("Could not parse Dash mpd", e);
        }
    }
}
