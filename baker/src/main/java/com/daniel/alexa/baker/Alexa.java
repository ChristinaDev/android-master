package com.daniel.alexa.baker;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.services.youtube.YoutubeService;

import java.util.List;

public class Alexa {
    private static Downloader downloader = null;

    private Alexa() {
    }

    public static void init(Downloader d) {
        downloader = d;
    }

    public static Downloader getDownloader() {
        return downloader;
    }


    public static List<YoutubeService> getServices() {
        return ServiceList.all();
    }

    public static StreamingService getService(int serviceId) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getServiceId() == serviceId) {
                return service;
            }
        }
        throw new ExtractionException("There's no service with the id = \"" + serviceId + "\"");
    }

    public static StreamingService getService(String serviceName) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getServiceInfo().getName().equals(serviceName)) {
                return service;
            }
        }
        throw new ExtractionException("There's no service with the name = \"" + serviceName + "\"");
    }

    public static StreamingService getServiceByUrl(String url) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getLinkTypeByUrl(url) != StreamingService.LinkType.NONE) {
                return service;
            }
        }
        throw new ExtractionException("No service can handle the url = \"" + url + "\"");
    }

    public static int getIdOfService(String serviceName) {
        try {
         
            return getService(serviceName).getServiceId();
        } catch (ExtractionException ignored) {
            return -1;
        }
    }

    public static String getNameOfService(int id) {
        try {
       
            return getService(id).getServiceInfo().getName();
        } catch (Exception e) {
            System.err.println("Service id not known");
            e.printStackTrace();
            return "<unknown>";
        }
    }
}
