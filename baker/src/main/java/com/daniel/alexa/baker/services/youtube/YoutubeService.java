package com.daniel.alexa.baker.services.youtube;

import com.daniel.alexa.baker.*;
import com.daniel.alexa.baker.linkhandler.*;
import com.daniel.alexa.baker.channel.ChannelBaker;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.kiosk.KioskBaker;
import com.daniel.alexa.baker.kiosk.KioskList;
import com.daniel.alexa.baker.playlist.PlaylistBaker;
import com.daniel.alexa.baker.search.SearchBaker;
import com.daniel.alexa.baker.services.youtube.bakers.*;
import com.daniel.alexa.baker.services.youtube.linkHandler.*;
import com.daniel.alexa.baker.stream.StreamBaker;
import com.daniel.alexa.baker.subscription.SubscriptionBaker;

import static java.util.Arrays.asList;
import static com.daniel.alexa.baker.StreamingService.ServiceInfo.MediaCapability.*;

public class YoutubeService extends StreamingService {

    public YoutubeService(int id) {
        super(id, "YouTube", asList(AUDIO, VIDEO, LIVE));
    }

    @Override
    public SearchBaker getSearchBaker(SearchQueryHandler query, String contentCountry) {
        return new YoutubeSearchBaker(this, query, contentCountry);
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return YoutubeStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return YoutubeChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return YoutubePlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return YoutubeSearchQueryHandlerFactory.getInstance();
    }

    @Override
    public StreamBaker getStreamBaker(LinkHandler linkHandler) throws ExtractionException {
        return new YoutubeStreamBaker(this, linkHandler);
    }

    @Override
    public ChannelBaker getChannelBaker(ListLinkHandler urlIdHandler) throws ExtractionException {
        return new YoutubeChannelBaker(this, urlIdHandler);
    }

    @Override
    public PlaylistBaker getPlaylistBaker(ListLinkHandler urlIdHandler) throws ExtractionException {
        return new YoutubePlaylistBaker(this, urlIdHandler);
    }

    @Override
    public SuggestionBaker getSuggestionBaker() {
        return new YoutubeSuggestionBaker(getServiceId());
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        KioskList list = new KioskList(getServiceId());

        try {
            list.addKioskEntry(new KioskList.KioskBakerFactory() {
                @Override
                public KioskBaker createNewKiosk(StreamingService streamingService, String url, String id)
                throws ExtractionException {
                    return new YoutubeTrendingBaker(YoutubeService.this,
                            new YoutubeTrendingLinkHandlerFactory().fromUrl(url), id);
                }
            }, new YoutubeTrendingLinkHandlerFactory(), "Trending");
            list.setDefaultKiosk("Trending");
        } catch (Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionBaker getSubscriptionBaker() {
        return new YoutubeSubscriptionBaker(this);
    }

}
