package com.daniel.alexa.baker;

import com.daniel.alexa.baker.exceptions.ParsingException;

public interface InfoItemBaker {
    String getName() throws ParsingException;
    String getUrl() throws ParsingException;
    String getThumbnailUrl() throws ParsingException;
}
