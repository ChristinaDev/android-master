package com.daniel.alexa.baker.playlist;

import com.daniel.alexa.baker.InfoItemsCollector;
import com.daniel.alexa.baker.exceptions.ParsingException;

public class PlaylistInfoItemsCollector extends InfoItemsCollector<PlaylistInfoItem, PlaylistInfoItemBaker> {

    public PlaylistInfoItemsCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public PlaylistInfoItem extract(PlaylistInfoItemBaker baker) throws ParsingException {

        String name = baker.getName();
        int serviceId = getServiceId();
        String url = baker.getUrl();

        PlaylistInfoItem resultItem = new PlaylistInfoItem(serviceId, url, name);

        try {
            resultItem.setUploaderName(baker.getUploaderName());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setThumbnailUrl(baker.getThumbnailUrl());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setStreamCount(baker.getStreamCount());
        } catch (Exception e) {
            addError(e);
        }
        return resultItem;
    }
}
