package com.daniel.alexa.baker.utils;

import com.daniel.alexa.baker.Info;
import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.InfoItemsCollector;
import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.ListBaker.InfoItemsPage;
import com.daniel.alexa.baker.stream.StreamBaker;
import com.daniel.alexa.baker.stream.StreamInfo;

import java.util.Collections;
import java.util.List;

public class BakerHelper {
    private BakerHelper() {}

    public static <T extends InfoItem> InfoItemsPage<T> getItemsPageOrLogError(Info info, ListBaker<T> baker) {
        try {
            InfoItemsPage<T> page = baker.getInitialPage();
            info.addAllErrors(page.getErrors());

            return page;
        } catch (Exception e) {
            info.addError(e);
            return InfoItemsPage.emptyPage();
        }
    }


    public static List<InfoItem> getRelatedVideosOrLogError(StreamInfo info, StreamBaker baker) {
        try {
            InfoItemsCollector<? extends InfoItem, ?> collector = baker.getRelatedVideos();
            info.addAllErrors(collector.getErrors());

           
            return (List<InfoItem>) collector.getItems();
        } catch (Exception e) {
            info.addError(e);
            return Collections.emptyList();
        }
    }
}
