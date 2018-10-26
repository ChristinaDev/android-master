package com.daniel.alexa.baker;


import com.daniel.alexa.baker.services.youtube.YoutubeService;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;


public final class ServiceList {
    private ServiceList() {
        //no instance
    }

    public static final YoutubeService YouTube;


    private static final List<YoutubeService> SERVICES = unmodifiableList(
            asList(
                    YouTube = new YoutubeService(0)
            ));

    /**
     * @return a unmodifiable list of all the supported services
     */
    public static List<YoutubeService> all() {
        return SERVICES;
    }
}
