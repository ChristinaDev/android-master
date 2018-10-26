package com.daniel.alexa.baker.kiosk;

import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.linkhandler.ListLinkHandlerFactory;
import com.daniel.alexa.baker.exceptions.ExtractionException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public  class KioskList {
    public interface KioskBakerFactory {
        KioskBaker createNewKiosk(final StreamingService streamingService,
                                             final String url,
                                             final String kioskId)
            throws ExtractionException, IOException;
    }

    private final int service_id;
    private final HashMap<String, KioskEntry> kioskList = new HashMap<>();
    private String defaultKiosk = null;

    private class KioskEntry {
        public KioskEntry(KioskBakerFactory ef, ListLinkHandlerFactory h) {
            bakerFactory = ef;
            handlerFactory = h;
        }
        final KioskBakerFactory bakerFactory;
        final ListLinkHandlerFactory handlerFactory;
    }

    public KioskList(int service_id) {
        this.service_id = service_id;
    }

    public void addKioskEntry(KioskBakerFactory bakerFactory, ListLinkHandlerFactory handlerFactory, String id)
        throws Exception {
        if(kioskList.get(id) != null) {
            throw new Exception("Kiosk with type " + id + " already exists.");
        }
        kioskList.put(id, new KioskEntry(bakerFactory, handlerFactory));
    }

    public void setDefaultKiosk(String kioskType) {
        defaultKiosk = kioskType;
    }

    public KioskBaker getDefaultKioskBaker(String nextPageUrl)
            throws ExtractionException, IOException {
        if(defaultKiosk != null && !defaultKiosk.equals("")) {
            return getBakerById(defaultKiosk, nextPageUrl);
        } else {
            if(!kioskList.isEmpty()) {
               
                Object[] keySet = kioskList.keySet().toArray();
                return getBakerById(keySet[0].toString(), nextPageUrl);
            } else {
                return null;
            }
        }
    }

    public String getDefaultKioskId() {
        return defaultKiosk;
    }

    public KioskBaker getBakerById(String kioskId, String nextPageUrl)
            throws ExtractionException, IOException {
        KioskEntry ke = kioskList.get(kioskId);
        if(ke == null) {
            throw new ExtractionException("No kiosk found with the type: " + kioskId);
        } else {
            return ke.bakerFactory.createNewKiosk(Alexa.getService(service_id),
                    ke.handlerFactory.fromId(kioskId).getUrl(), kioskId);
        }
    }

    public Set<String> getAvailableKiosks() {
        return kioskList.keySet();
    }

    public KioskBaker getBakerByUrl(String url, String nextPageUrl)
            throws ExtractionException, IOException {
        for(Map.Entry<String, KioskEntry> e : kioskList.entrySet()) {
            KioskEntry ke = e.getValue();
            if(ke.handlerFactory.acceptUrl(url)) {
                return getBakerById(e.getKey(), nextPageUrl);
            }
        }
        throw new ExtractionException("Could not find a kiosk that fits to the url: " + url);
    }

    public ListLinkHandlerFactory getListLinkHandlerFactoryByType(String type) {
        return kioskList.get(type).handlerFactory;
    }
}
