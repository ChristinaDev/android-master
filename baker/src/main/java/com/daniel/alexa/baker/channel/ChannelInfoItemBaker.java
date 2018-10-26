package com.daniel.alexa.baker.channel;

import com.daniel.alexa.baker.InfoItemBaker;
import com.daniel.alexa.baker.exceptions.ParsingException;


public interface ChannelInfoItemBaker extends InfoItemBaker {
    String getDescription() throws ParsingException;

    long getSubscriberCount() throws ParsingException;
    long getStreamCount() throws ParsingException;
}
