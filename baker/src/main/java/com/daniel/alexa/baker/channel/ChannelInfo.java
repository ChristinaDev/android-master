package com.daniel.alexa.baker.channel;

import com.daniel.alexa.baker.ListBaker.InfoItemsPage;
import com.daniel.alexa.baker.ListInfo;
import com.daniel.alexa.baker.Alexa;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.stream.StreamInfoItem;
import com.daniel.alexa.baker.linkhandler.ListLinkHandler;
import com.daniel.alexa.baker.utils.BakerHelper;

import java.io.IOException;


public class ChannelInfo extends ListInfo<StreamInfoItem> {

    public ChannelInfo(int serviceId, ListLinkHandler urlIdHandler, String name) throws ParsingException {
        super(serviceId, urlIdHandler, name);
    }

    public static ChannelInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(Alexa.getServiceByUrl(url), url);
    }

    public static ChannelInfo getInfo(StreamingService service, String url) throws IOException, ExtractionException {
        ChannelBaker baker = service.getChannelBaker(url);
        baker.fetchPage();
        return getInfo(baker);
    }

    public static InfoItemsPage<StreamInfoItem> getMoreItems(StreamingService service, String url, String pageUrl) throws IOException, ExtractionException {
        return service.getChannelBaker(url).getPage(pageUrl);
    }

    public static ChannelInfo getInfo(ChannelBaker baker) throws IOException, ExtractionException {

        ChannelInfo info = new ChannelInfo(baker.getServiceId(),
                baker.getUIHandler(),
                baker.getName());


        try {
            info.setAvatarUrl(baker.getAvatarUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setBannerUrl(baker.getBannerUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setFeedUrl(baker.getFeedUrl());
        } catch (Exception e) {
            info.addError(e);
        }

        final InfoItemsPage<StreamInfoItem> itemsPage = BakerHelper.getItemsPageOrLogError(info, baker);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPageUrl(itemsPage.getNextPageUrl());

        try {
            info.setSubscriberCount(baker.getSubscriberCount());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setDescription(baker.getDescription());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setDonationLinks(baker.getDonationLinks());
        } catch (Exception e) {
            info.addError(e);
        }

        return info;
    }

    private String avatarUrl;
    private String bannerUrl;
    private String feedUrl;
    private long subscriberCount = -1;
    private String description;
    private String[] donationLinks;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public long getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(long subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getDonationLinks() {
        return donationLinks;
    }

    public void setDonationLinks(String[] donationLinks) {
        this.donationLinks = donationLinks;
    }
}
