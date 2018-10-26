package com.daniel.alexa.baker.search;

import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.InfoItemBaker;
import com.daniel.alexa.baker.InfoItemsCollector;
import com.daniel.alexa.baker.channel.ChannelInfoItemBaker;
import com.daniel.alexa.baker.channel.ChannelInfoItemsCollector;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.playlist.PlaylistInfoItemBaker;
import com.daniel.alexa.baker.playlist.PlaylistInfoItemsCollector;
import com.daniel.alexa.baker.stream.StreamInfoItemBaker;
import com.daniel.alexa.baker.stream.StreamInfoItemsCollector;


/**
 * Collector for search results
 *
 * This collector can handle the following baker types:
 * <ul>
 *     <li>{@link StreamInfoItemBaker}</li>
 *     <li>{@link ChannelInfoItemBaker}</li>
 *     <li>{@link PlaylistInfoItemBaker}</li>
 * </ul>
 * Calling {@link #extract(InfoItemBaker)} or {@link #commit(Object)} with any
 * other baker type will raise an exception.
 */
public class InfoItemsSearchCollector extends InfoItemsCollector<InfoItem, InfoItemBaker> {
    private final StreamInfoItemsCollector streamCollector;
    private final ChannelInfoItemsCollector userCollector;
    private final PlaylistInfoItemsCollector playlistCollector;

    InfoItemsSearchCollector(int serviceId) {
        super(serviceId);
        streamCollector = new StreamInfoItemsCollector(serviceId);
        userCollector = new ChannelInfoItemsCollector(serviceId);
        playlistCollector = new PlaylistInfoItemsCollector(serviceId);
    }

    @Override
    public InfoItem extract(InfoItemBaker baker) throws ParsingException {

        if(baker instanceof StreamInfoItemBaker) {
            return streamCollector.extract((StreamInfoItemBaker) baker);
        } else if(baker instanceof ChannelInfoItemBaker) {
            return userCollector.extract((ChannelInfoItemBaker) baker);
        } else if(baker instanceof PlaylistInfoItemBaker) {
            return playlistCollector.extract((PlaylistInfoItemBaker) baker);
        } else {
            throw new IllegalArgumentException("Invalid baker type: " + baker);
        }
    }
}
