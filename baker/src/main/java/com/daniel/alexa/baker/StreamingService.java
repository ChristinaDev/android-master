package com.daniel.alexa.baker;

import com.daniel.alexa.baker.channel.ChannelBaker;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.kiosk.KioskList;
import com.daniel.alexa.baker.playlist.PlaylistBaker;
import com.daniel.alexa.baker.search.SearchBaker;
import com.daniel.alexa.baker.linkhandler.*;
import com.daniel.alexa.baker.stream.StreamBaker;
import com.daniel.alexa.baker.subscription.SubscriptionBaker;

import java.util.Collections;
import java.util.List;

public abstract class StreamingService {
    public static class ServiceInfo {
        private final String name;
        private final List<MediaCapability> mediaCapabilities;

        public ServiceInfo(String name, List<MediaCapability> mediaCapabilities) {
            this.name = name;
            this.mediaCapabilities = Collections.unmodifiableList(mediaCapabilities);
        }

        public String getName() {
            return name;
        }

        public List<MediaCapability> getMediaCapabilities() {
            return mediaCapabilities;
        }

        public enum MediaCapability {
            AUDIO, VIDEO, LIVE
        }
    }

    public enum LinkType {
        NONE,
        STREAM,
        CHANNEL,
        PLAYLIST
    }

    private final int serviceId;
    private final ServiceInfo serviceInfo;

    public StreamingService(int id, String name, List<ServiceInfo.MediaCapability> capabilities) {
        this.serviceId = id;
        this.serviceInfo = new ServiceInfo(name, capabilities);
    }

    public final int getServiceId() {
        return serviceId;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    @Override
    public String toString() {
        return serviceId + ":" + serviceInfo.getName();
    }

    ////////////////////////////////////////////
    // Url Id handler
    ////////////////////////////////////////////
    public abstract LinkHandlerFactory getStreamLHFactory();
    public abstract ListLinkHandlerFactory getChannelLHFactory();
    public abstract ListLinkHandlerFactory getPlaylistLHFactory();
    public abstract SearchQueryHandlerFactory getSearchQHFactory();


    ////////////////////////////////////////////
    // baker
    ////////////////////////////////////////////
    public abstract SearchBaker getSearchBaker(SearchQueryHandler queryHandler, String contentCountry);
    public abstract SuggestionBaker getSuggestionBaker();
    public abstract SubscriptionBaker getSubscriptionBaker();
    public abstract KioskList getKioskList() throws ExtractionException;

    public abstract ChannelBaker getChannelBaker(ListLinkHandler urlIdHandler) throws ExtractionException;
    public abstract PlaylistBaker getPlaylistBaker(ListLinkHandler urlIdHandler) throws ExtractionException;
    public abstract StreamBaker getStreamBaker(LinkHandler UIHFactory) throws ExtractionException;

    public SearchBaker getSearchBaker(String query, List<String> contentFilter, String sortFilter, String contentCountry) throws ExtractionException {
        return getSearchBaker(getSearchQHFactory().fromQuery(query, contentFilter, sortFilter), contentCountry);
    }

    public ChannelBaker getChannelBaker(String id, List<String> contentFilter, String sortFilter) throws ExtractionException {
        return getChannelBaker(getChannelLHFactory().fromQuery(id, contentFilter, sortFilter));
    }

    public PlaylistBaker getPlaylistBaker(String id, List<String> contentFilter, String sortFilter) throws ExtractionException {
        return getPlaylistBaker(getPlaylistLHFactory().fromQuery(id, contentFilter, sortFilter));
    }

    public SearchBaker getSearchBaker(String query, String contentCountry) throws ExtractionException {
        return getSearchBaker(getSearchQHFactory().fromQuery(query), contentCountry);
    }

    public ChannelBaker getChannelBaker(String url) throws ExtractionException {
        return getChannelBaker(getChannelLHFactory().fromUrl(url));
    }

    public PlaylistBaker getPlaylistBaker(String url) throws ExtractionException {
        return getPlaylistBaker(getPlaylistLHFactory().fromUrl(url));
    }

    public StreamBaker getStreamBaker(String url) throws ExtractionException {
        return getStreamBaker(getStreamLHFactory().fromUrl(url));
    }




    public final LinkType getLinkTypeByUrl(String url) throws ParsingException {
        LinkHandlerFactory sH = getStreamLHFactory();
        LinkHandlerFactory cH = getChannelLHFactory();
        LinkHandlerFactory pH = getPlaylistLHFactory();

        if (sH.acceptUrl(url)) {
            return LinkType.STREAM;
        } else if (cH.acceptUrl(url)) {
            return LinkType.CHANNEL;
        } else if (pH.acceptUrl(url)) {
            return LinkType.PLAYLIST;
        } else {
            return LinkType.NONE;
        }
    }
}
