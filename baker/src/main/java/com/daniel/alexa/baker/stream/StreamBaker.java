package com.daniel.alexa.baker.stream;

import com.daniel.alexa.baker.Baker;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.Subtitles;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.linkhandler.LinkHandler;
import com.daniel.alexa.baker.utils.Parser;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;


public abstract class StreamBaker extends Baker {

    public static final int NO_AGE_LIMIT = 0;

    public StreamBaker(StreamingService service, LinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Nonnull
    public abstract String getUploadDate() throws ParsingException;
    @Nonnull
    public abstract String getThumbnailUrl() throws ParsingException;
    @Nonnull
    public abstract String getDescription() throws ParsingException;

    /**
     * Get the age limit
     * @return The age which limits the content or {@value NO_AGE_LIMIT} if there is no limit
     * @throws ParsingException if an error occurs while parsing
     */
    public abstract int getAgeLimit() throws ParsingException;

    public abstract long getLength() throws ParsingException;
    public abstract long getTimeStamp() throws ParsingException;
    protected long getTimestampSeconds(String regexPattern) throws ParsingException {
        String timeStamp;
        try {
            timeStamp = Parser.matchGroup1(regexPattern, getOriginalUrl());
        } catch (Parser.RegexException e) {
           
            return -2;
        }

        if (!timeStamp.isEmpty()) {
            try {
                String secondsString = "";
                String minutesString = "";
                String hoursString = "";
                try {
                    secondsString = Parser.matchGroup1("(\\d{1,3})s", timeStamp);
                    minutesString = Parser.matchGroup1("(\\d{1,3})m", timeStamp);
                    hoursString = Parser.matchGroup1("(\\d{1,3})h", timeStamp);
                } catch (Exception e) {
          
                    if (secondsString.isEmpty() //if nothing was got,
                            && minutesString.isEmpty()//treat as unlabelled seconds
                            && hoursString.isEmpty()) {
                        secondsString = Parser.matchGroup1("t=(\\d+)", timeStamp);
                    }
                }

                int seconds = secondsString.isEmpty() ? 0 : Integer.parseInt(secondsString);
                int minutes = minutesString.isEmpty() ? 0 : Integer.parseInt(minutesString);
                int hours = hoursString.isEmpty() ? 0 : Integer.parseInt(hoursString);

             
                return seconds + (60 * minutes) + (3600 * hours);
               
            } catch (ParsingException e) {
                throw new ParsingException("Could not get timestamp.", e);
            }
        } else {
            return 0;
        }};

    public abstract long getViewCount() throws ParsingException;
    public abstract long getLikeCount() throws ParsingException;
    public abstract long getDislikeCount() throws ParsingException;

    @Nonnull
    public abstract String getUploaderUrl() throws ParsingException;
    @Nonnull
    public abstract String getUploaderName() throws ParsingException;
    @Nonnull
    public abstract String getUploaderAvatarUrl() throws ParsingException;

    /**
     * Get the dash mpd url
     * @return the url as a string or an empty string
     * @throws ParsingException if an error occurs while reading
     */
    @Nonnull public abstract String getDashMpdUrl() throws ParsingException;
    @Nonnull public abstract String getHlsUrl() throws ParsingException;
    public abstract List<AudioStream> getAudioStreams() throws IOException, ExtractionException;
    public abstract List<VideoStream> getVideoStreams() throws IOException, ExtractionException;
    public abstract List<VideoStream> getVideoOnlyStreams() throws IOException, ExtractionException;

    @Nonnull
    public abstract List<Subtitles> getSubtitlesDefault() throws IOException, ExtractionException;
    @Nonnull
    public abstract List<Subtitles> getSubtitles(SubtitlesFormat format) throws IOException, ExtractionException;

    public abstract StreamType getStreamType() throws ParsingException;
    public abstract StreamInfoItem getNextVideo() throws IOException, ExtractionException;
    public abstract StreamInfoItemsCollector getRelatedVideos() throws IOException, ExtractionException;

    /**
     * Analyses the webpage's document and extracts any error message there might be.
     *
     * @return Error message; null if there is no error message.
     */
    public abstract String getErrorMessage();
}
