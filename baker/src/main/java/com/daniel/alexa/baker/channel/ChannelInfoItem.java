package com.daniel.alexa.baker.channel;

import com.daniel.alexa.baker.InfoItem;


public class ChannelInfoItem extends InfoItem {

    private String description;
    private long subscriberCount = -1;
    private long streamCount = -1;


    public ChannelInfoItem(int serviceId, String url, String name) {
        super(InfoType.CHANNEL, serviceId, url, name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(long subscriber_count) {
        this.subscriberCount = subscriber_count;
    }

    public long getStreamCount() {
        return streamCount;
    }

    public void setStreamCount(long stream_count) {
        this.streamCount = stream_count;
    }
}
