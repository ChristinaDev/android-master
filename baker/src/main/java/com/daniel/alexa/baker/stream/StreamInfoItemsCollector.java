package com.daniel.alexa.baker.stream;

import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.InfoItemsCollector;
import com.daniel.alexa.baker.exceptions.FoundAdException;
import com.daniel.alexa.baker.exceptions.ParsingException;

import java.util.List;
import java.util.Vector;

public class StreamInfoItemsCollector extends InfoItemsCollector<StreamInfoItem, StreamInfoItemBaker> {

    public StreamInfoItemsCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public StreamInfoItem extract(StreamInfoItemBaker baker) throws ParsingException {
        if (baker.isAd()) {
            throw new FoundAdException("Found ad");
        }

        int serviceId = getServiceId();
        String url = baker.getUrl();
        String name = baker.getName();
        StreamType streamType = baker.getStreamType();

        StreamInfoItem resultItem = new StreamInfoItem(serviceId, url, name, streamType);


        try {
            resultItem.setDuration(baker.getDuration());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setUploaderName(baker.getUploaderName());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setUploadDate(baker.getUploadDate());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setViewCount(baker.getViewCount());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setThumbnailUrl(baker.getThumbnailUrl());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setUploaderUrl(baker.getUploaderUrl());
        } catch (Exception e) {
            addError(e);
        }
        return resultItem;
    }

    @Override
    public void commit(StreamInfoItemBaker baker) {
        try {
            addItem(extract(baker));
        } catch (FoundAdException ae) {
      
        } catch (Exception e) {
            addError(e);
        }
    }

    public List<StreamInfoItem> getStreamInfoItemList() {
        List<StreamInfoItem> siiList = new Vector<>();
        for(InfoItem ii : super.getItems()) {
            if(ii instanceof StreamInfoItem) {
                siiList.add((StreamInfoItem) ii);
            }
        }
        return siiList;
    }
}
