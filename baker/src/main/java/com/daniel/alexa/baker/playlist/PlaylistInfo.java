package com.daniel.alexa.baker.playlist;

import com.daniel.alexa.baker.ListBaker.InfoItemsPage;
import com.daniel.alexa.baker.ListInfo;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.linkhandler.ListLinkHandler;
import com.daniel.alexa.baker.stream.StreamInfoItem;
import com.daniel.alexa.baker.utils.BakerHelper;

import java.io.IOException;

public class PlaylistInfo extends ListInfo<StreamInfoItem> {

    private PlaylistInfo(int serviceId, ListLinkHandler urlIdHandler, String name) throws ParsingException {
        super(serviceId, urlIdHandler, name);
    }

    public static PlaylistInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(Alexa.getServiceByUrl(url), url);
    }

    public static PlaylistInfo getInfo(StreamingService service, String url) throws IOException, ExtractionException {
        PlaylistBaker baker = service.getPlaylistBaker(url);
        baker.fetchPage();
        return getInfo(baker);
    }

    public static InfoItemsPage<StreamInfoItem> getMoreItems(StreamingService service, String url, String pageUrl) throws IOException, ExtractionException {
        return service.getPlaylistBaker(url).getPage(pageUrl);
    }

    /**
     * Get PlaylistInfo from PlaylistBaker
     *
     * @param baker an baker where fetchPage() was already got called on.
     */
    public static PlaylistInfo getInfo(PlaylistBaker baker) throws ExtractionException {

        final PlaylistInfo info = new PlaylistInfo(
                baker.getServiceId(),
                baker.getUIHandler(),
                baker.getName());

        try {
            info.setStreamCount(baker.getStreamCount());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setThumbnailUrl(baker.getThumbnailUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setUploaderUrl(baker.getUploaderUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setUploaderName(baker.getUploaderName());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setUploaderAvatarUrl(baker.getUploaderAvatarUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setBannerUrl(baker.getBannerUrl());
        } catch (Exception e) {
            info.addError(e);
        }

        final InfoItemsPage<StreamInfoItem> itemsPage = BakerHelper.getItemsPageOrLogError(info, baker);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPageUrl(itemsPage.getNextPageUrl());

        return info;
    }

    private String thumbnailUrl;
    private String bannerUrl;
    private String uploaderUrl;
    private String uploaderName;
    private String uploaderAvatarUrl;
    private long streamCount = 0;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getUploaderUrl() {
        return uploaderUrl;
    }

    public void setUploaderUrl(String uploaderUrl) {
        this.uploaderUrl = uploaderUrl;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderAvatarUrl() {
        return uploaderAvatarUrl;
    }

    public void setUploaderAvatarUrl(String uploaderAvatarUrl) {
        this.uploaderAvatarUrl = uploaderAvatarUrl;
    }

    public long getStreamCount() {
        return streamCount;
    }

    public void setStreamCount(long streamCount) {
        this.streamCount = streamCount;
    }
}
