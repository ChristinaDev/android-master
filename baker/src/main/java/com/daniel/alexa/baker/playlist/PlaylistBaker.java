package com.daniel.alexa.baker.playlist;

import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.linkhandler.ListLinkHandler;
import com.daniel.alexa.baker.stream.StreamInfoItem;

public abstract class PlaylistBaker extends ListBaker<StreamInfoItem> {

    public PlaylistBaker(StreamingService service, ListLinkHandler urlIdHandler) {
        super(service, urlIdHandler);
    }

    public abstract String getThumbnailUrl() throws ParsingException;
    public abstract String getBannerUrl() throws ParsingException;

    public abstract String getUploaderUrl() throws ParsingException;
    public abstract String getUploaderName() throws ParsingException;
    public abstract String getUploaderAvatarUrl() throws ParsingException;

    public abstract long getStreamCount() throws ParsingException;
}
