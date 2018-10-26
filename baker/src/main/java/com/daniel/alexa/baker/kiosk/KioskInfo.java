package com.daniel.alexa.baker.kiosk;


import com.daniel.alexa.baker.*;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.stream.StreamInfoItem;
import com.daniel.alexa.baker.linkhandler.ListLinkHandler;
import com.daniel.alexa.baker.utils.BakerHelper;

import java.io.IOException;

public class KioskInfo extends ListInfo<StreamInfoItem> {

    private KioskInfo(int serviceId, ListLinkHandler urlIdHandler, String name) throws ParsingException {
        super(serviceId, urlIdHandler, name);
    }

    public static ListBaker.InfoItemsPage<StreamInfoItem> getMoreItems(StreamingService service,
                                                                           String url,
                                                                           String pageUrl,
                                                                           String contentCountry) throws IOException, ExtractionException {
        KioskList kl = service.getKioskList();
        KioskBaker baker = kl.getBakerByUrl(url, pageUrl);
        baker.setContentCountry(contentCountry);
        return baker.getPage(pageUrl);
    }

    public static KioskInfo getInfo(String url,
                                    String contentCountry) throws IOException, ExtractionException {
        return getInfo(Alexa.getServiceByUrl(url), url, contentCountry);
    }

    public static KioskInfo getInfo(StreamingService service,
                                    String url,
                                    String contentCountry) throws IOException, ExtractionException {
        KioskList kl = service.getKioskList();
        KioskBaker baker = kl.getBakerByUrl(url, null);
        baker.setContentCountry(contentCountry);
        baker.fetchPage();
        return getInfo(baker);
    }

    /**
     * Get KioskInfo from KioskBaker
     *
     * @param baker an baker where fetchPage() was already got called on.
     */
    public static KioskInfo getInfo(KioskBaker baker) throws ExtractionException {

        final KioskInfo info = new KioskInfo(baker.getServiceId(),
                baker.getUIHandler(),
                baker.getName());

        final ListBaker.InfoItemsPage<StreamInfoItem> itemsPage = BakerHelper.getItemsPageOrLogError(info, baker);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPageUrl(itemsPage.getNextPageUrl());

        return info;
    }
}
