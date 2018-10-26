package com.daniel.alexa.baker.channel;

import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.stream.StreamInfoItem;
import com.daniel.alexa.baker.linkhandler.ListLinkHandler;

public abstract class ChannelBaker extends ListBaker<StreamInfoItem> {

    public ChannelBaker(StreamingService service, ListLinkHandler urlIdHandler) {
        super(service, urlIdHandler);
    }

    public abstract String getAvatarUrl() throws ParsingException;
    public abstract String getBannerUrl() throws ParsingException;
    public abstract String getFeedUrl() throws ParsingException;
    public abstract long getSubscriberCount() throws ParsingException;
    public abstract String getDescription() throws ParsingException;
    public abstract String[] getDonationLinks() throws ParsingException;
}
