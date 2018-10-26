package com.daniel.alexa.baker.stream;

import com.daniel.alexa.baker.MediaFormat;

public class AudioStream extends Stream {
    public int average_bitrate = -1;

    /**
     * Create a new audio stream
     * @param url the url
     * @param format the format
     * @param averageBitrate the average bitrate
     */
    public AudioStream(String url, MediaFormat format, int averageBitrate) {
        super(url, format);
        this.average_bitrate = averageBitrate;
    }

    @Override
    public boolean equalStats(Stream cmp) {
        return super.equalStats(cmp) && cmp instanceof AudioStream &&
                average_bitrate == ((AudioStream) cmp).average_bitrate;
    }

    /**
     * Get the average bitrate
     * @return the average bitrate or -1
     */
    public int getAverageBitrate() {
        return average_bitrate;
    }
}
