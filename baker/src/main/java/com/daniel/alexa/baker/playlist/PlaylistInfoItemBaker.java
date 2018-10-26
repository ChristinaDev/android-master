package com.daniel.alexa.baker.playlist;

import com.daniel.alexa.baker.InfoItemBaker;
import com.daniel.alexa.baker.exceptions.ParsingException;

public interface PlaylistInfoItemBaker extends InfoItemBaker {

    /**
     * Get the uploader name
     * @return the uploader name
     * @throws ParsingException
     */
    String getUploaderName() throws ParsingException;

    /**
     * Get the number of streams
     * @return the number of streams
     * @throws ParsingException
     */
    long getStreamCount() throws ParsingException;
}
