package com.daniel.alexa.baker.stream;

import com.daniel.alexa.baker.*;
import com.daniel.alexa.baker.exceptions.ContentNotAvailableException;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.utils.DashMpdParser;
import com.daniel.alexa.baker.utils.BakerHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Info object for opened videos, ie the video ready to play.
 */
public class StreamInfo extends Info {

    public static class StreamExtractException extends ExtractionException {
        StreamExtractException(String message) {
            super(message);
        }
    }

    public StreamInfo(int serviceId, String url, String originalUrl, StreamType streamType, String id, String name, int ageLimit) {
        super(serviceId, id, url, originalUrl, name);
        this.streamType = streamType;
        this.ageLimit = ageLimit;
    }

    public static StreamInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(Alexa.getServiceByUrl(url), url);
    }

    public static StreamInfo getInfo(StreamingService service, String url) throws IOException, ExtractionException {
        return getInfo(service.getStreamBaker(url));
    }

    private static StreamInfo getInfo(StreamBaker baker) throws ExtractionException, IOException {
        baker.fetchPage();
        StreamInfo streamInfo;
        try {
            streamInfo = extractImportantData(baker);
            streamInfo = extractStreams(streamInfo, baker);
            streamInfo = extractOptionalData(streamInfo, baker);
        } catch (ExtractionException e) {
       
            String errorMsg = baker.getErrorMessage();

            if (errorMsg != null) {
                throw new ContentNotAvailableException(errorMsg);
            } else {
                throw e;
            }
        }

        return streamInfo;
    }

    private static StreamInfo extractImportantData(StreamBaker baker) throws ExtractionException {
  

        int serviceId = baker.getServiceId();
        String url = baker.getUrl();
        String originalUrl = baker.getOriginalUrl();
        StreamType streamType = baker.getStreamType();
        String id = baker.getId();
        String name = baker.getName();
        int ageLimit = baker.getAgeLimit();

        if ((streamType == StreamType.NONE)
                || (url == null || url.isEmpty())
                || (id == null || id.isEmpty())
                || (name == null /* streamInfo.title can be empty of course */)
                || (ageLimit == -1)) {
            throw new ExtractionException("Some important stream information was not given.");
        }

        return new StreamInfo(serviceId, url, originalUrl, streamType, id, name, ageLimit);
    }

    private static StreamInfo extractStreams(StreamInfo streamInfo, StreamBaker baker) throws ExtractionException {
    

        try {
            streamInfo.setDashMpdUrl(baker.getDashMpdUrl());
        } catch (Exception e) {
            streamInfo.addError(new ExtractionException("Couldn't get Dash manifest", e));
        }

        try {
            streamInfo.setHlsUrl(baker.getHlsUrl());
        } catch (Exception e) {
            streamInfo.addError(new ExtractionException("Couldn't get HLS manifest", e));
        }

        /*  Load and extract audio */
        try {
            streamInfo.setAudioStreams(baker.getAudioStreams());
        } catch (Exception e) {
            streamInfo.addError(new ExtractionException("Couldn't get audio streams", e));
        }
        /* Extract video stream url*/
        try {
            streamInfo.setVideoStreams(baker.getVideoStreams());
        } catch (Exception e) {
            streamInfo.addError(new ExtractionException("Couldn't get video streams", e));
        }
        /* Extract video only stream url*/
        try {
            streamInfo.setVideoOnlyStreams(baker.getVideoOnlyStreams());
        } catch (Exception e) {
            streamInfo.addError(new ExtractionException("Couldn't get video only streams", e));
        }

    
        if (streamInfo.getVideoStreams() == null) streamInfo.setVideoStreams(new ArrayList<VideoStream>());
        if (streamInfo.getVideoOnlyStreams() == null) streamInfo.setVideoOnlyStreams(new ArrayList<VideoStream>());
        if (streamInfo.getAudioStreams() == null) streamInfo.setAudioStreams(new ArrayList<AudioStream>());

        Exception dashMpdError = null;
        if (streamInfo.getDashMpdUrl() != null && !streamInfo.getDashMpdUrl().isEmpty()) {
            try {
                DashMpdParser.ParserResult result = DashMpdParser.getStreams(streamInfo);
                streamInfo.getVideoOnlyStreams().addAll(result.getVideoOnlyStreams());
                streamInfo.getAudioStreams().addAll(result.getAudioStreams());
                streamInfo.getVideoStreams().addAll(result.getVideoStreams());
            } catch (Exception e) {
             
                dashMpdError = e;
            }
        }

        
        if ((streamInfo.videoStreams.isEmpty())
                && (streamInfo.audioStreams.isEmpty())) {

            if (dashMpdError != null) {
          
                streamInfo.addError(dashMpdError);
            }

            throw new StreamExtractException("Could not get any stream. See error variable to get further details.");
        }

        return streamInfo;
    }

    private static StreamInfo extractOptionalData(StreamInfo streamInfo, StreamBaker baker) {
 

        try {
            streamInfo.setThumbnailUrl(baker.getThumbnailUrl());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setDuration(baker.getLength());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setUploaderName(baker.getUploaderName());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setUploaderUrl(baker.getUploaderUrl());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setDescription(baker.getDescription());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setViewCount(baker.getViewCount());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setUploadDate(baker.getUploadDate());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setUploaderAvatarUrl(baker.getUploaderAvatarUrl());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setStartPosition(baker.getTimeStamp());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setLikeCount(baker.getLikeCount());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setDislikeCount(baker.getDislikeCount());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setNextVideo(baker.getNextVideo());
        } catch (Exception e) {
            streamInfo.addError(e);
        }
        try {
            streamInfo.setSubtitles(baker.getSubtitlesDefault());
        } catch (Exception e) {
            streamInfo.addError(e);
        }

        streamInfo.setRelatedStreams(BakerHelper.getRelatedVideosOrLogError(streamInfo, baker));
        return streamInfo;
    }

    private StreamType streamType;
    private String thumbnailUrl;
    private String uploadDate;
    private long duration = -1;
    private int ageLimit = -1;
    private String description;

    private long viewCount = -1;
    private long likeCount = -1;
    private long dislikeCount = -1;

    private String uploaderName;
    private String uploaderUrl;
    private String uploaderAvatarUrl;

    private List<VideoStream> videoStreams;
    private List<AudioStream> audioStreams;
    private List<VideoStream> videoOnlyStreams;

    private String dashMpdUrl;
    private String hlsUrl;
    private StreamInfoItem nextVideo;
    private List<InfoItem> relatedStreams;

    private long startPosition = 0;
    private List<Subtitles> subtitles;

    /**
     * Get the stream type
     *
     * @return the stream type
     */
    public StreamType getStreamType() {
        return streamType;
    }

    public void setStreamType(StreamType streamType) {
        this.streamType = streamType;
    }

    /**
     * Get the thumbnail url
     *
     * @return the thumbnail url as a string
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    /**
     * Get the duration in seconds
     *
     * @return the duration in seconds
     */
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(int ageLimit) {
        this.ageLimit = ageLimit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    /**
     * Get the number of likes.
     *
     * @return The number of likes or -1 if this information is not available
     */
    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * Get the number of dislikes.
     *
     * @return The number of likes or -1 if this information is not available
     */
    public long getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderUrl() {
        return uploaderUrl;
    }

    public void setUploaderUrl(String uploaderUrl) {
        this.uploaderUrl = uploaderUrl;
    }

    public String getUploaderAvatarUrl() {
        return uploaderAvatarUrl;
    }

    public void setUploaderAvatarUrl(String uploaderAvatarUrl) {
        this.uploaderAvatarUrl = uploaderAvatarUrl;
    }

    public List<VideoStream> getVideoStreams() {
        return videoStreams;
    }

    public void setVideoStreams(List<VideoStream> videoStreams) {
        this.videoStreams = videoStreams;
    }

    public List<AudioStream> getAudioStreams() {
        return audioStreams;
    }

    public void setAudioStreams(List<AudioStream> audioStreams) {
        this.audioStreams = audioStreams;
    }

    public List<VideoStream> getVideoOnlyStreams() {
        return videoOnlyStreams;
    }

    public void setVideoOnlyStreams(List<VideoStream> videoOnlyStreams) {
        this.videoOnlyStreams = videoOnlyStreams;
    }

    public String getDashMpdUrl() {
        return dashMpdUrl;
    }

    public void setDashMpdUrl(String dashMpdUrl) {
        this.dashMpdUrl = dashMpdUrl;
    }

    public String getHlsUrl() {
        return hlsUrl;
    }

    public void setHlsUrl(String hlsUrl) {
        this.hlsUrl = hlsUrl;
    }

    public StreamInfoItem getNextVideo() {
        return nextVideo;
    }

    public void setNextVideo(StreamInfoItem nextVideo) {
        this.nextVideo = nextVideo;
    }

    public List<InfoItem> getRelatedStreams() {
        return relatedStreams;
    }

    public void setRelatedStreams(List<InfoItem> relatedStreams) {
        this.relatedStreams = relatedStreams;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public List<Subtitles> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Subtitles> subtitles) {
        this.subtitles = subtitles;
    }

}
