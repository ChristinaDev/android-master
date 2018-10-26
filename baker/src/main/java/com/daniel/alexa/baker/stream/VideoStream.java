package com.daniel.alexa.baker.stream;

import com.daniel.alexa.baker.MediaFormat;

public class VideoStream extends Stream {
    public final String resolution;
    public final boolean isVideoOnly;


    public VideoStream(String url, MediaFormat format, String resolution) {
        this(url, format, resolution, false);
    }

    public VideoStream(String url, MediaFormat format, String resolution, boolean isVideoOnly) {
        super(url, format);
        this.resolution = resolution;
        this.isVideoOnly = isVideoOnly;
    }

    @Override
    public boolean equalStats(Stream cmp) {
        return super.equalStats(cmp) && cmp instanceof VideoStream &&
                resolution.equals(((VideoStream) cmp).resolution) &&
                isVideoOnly == ((VideoStream) cmp).isVideoOnly;
    }

    /**
     * Get the video resolution
     * @return the video resolution
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * Check if the video is video only.
     *
     * Video only streams have no audio
     * @return {@code true} if this stream is vid
     */
    public boolean isVideoOnly() {
        return isVideoOnly;
    }
}
