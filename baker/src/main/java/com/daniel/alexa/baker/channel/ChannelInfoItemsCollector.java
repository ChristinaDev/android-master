package com.daniel.alexa.baker.channel;

import com.daniel.alexa.baker.InfoItemsCollector;
import com.daniel.alexa.baker.exceptions.ParsingException;

public class ChannelInfoItemsCollector extends InfoItemsCollector<ChannelInfoItem, ChannelInfoItemBaker> {
    public ChannelInfoItemsCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public ChannelInfoItem extract(ChannelInfoItemBaker baker) throws ParsingException {
        // important information
        int serviceId = getServiceId();
        String name = baker.getName();
        String  url = baker.getUrl();

        ChannelInfoItem resultItem = new ChannelInfoItem(serviceId, url, name);


        // optional information
        try {
            resultItem.setSubscriberCount(baker.getSubscriberCount());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setStreamCount(baker.getStreamCount());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setThumbnailUrl(baker.getThumbnailUrl());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setDescription(baker.getDescription());
        } catch (Exception e) {
            addError(e);
        }
        return resultItem;
    }
}
