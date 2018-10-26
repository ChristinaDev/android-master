package com.daniel.alexa.baker.services.youtube.bakers;

import org.jsoup.nodes.Element;
import com.daniel.alexa.baker.channel.ChannelInfoItemBaker;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.utils.Utils;

public class YoutubeChannelInfoItemBaker implements ChannelInfoItemBaker {
    private final Element el;

    public YoutubeChannelInfoItemBaker(Element el) {
        this.el = el;
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        Element img = el.select("span[class*=\"yt-thumb-simple\"]").first()
                .select("img").first();

        String url = img.attr("abs:src");

        if (url.contains("gif")) {
            url = img.attr("abs:data-thumb");
        }
        return url;
    }

    @Override
    public String getName() throws ParsingException {
        return el.select("a[class*=\"yt-uix-tile-link\"]").first()
                .text();
    }

    @Override
    public String getUrl() throws ParsingException {
        return el.select("a[class*=\"yt-uix-tile-link\"]").first()
                .attr("abs:href");
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        final Element subsEl = el.select("span[class*=\"yt-subscriber-count\"]").first();
        if (subsEl != null) {
            try {
                return Long.parseLong(Utils.removeNonDigitCharacters(subsEl.text()));
            } catch (NumberFormatException e) {
                throw new ParsingException("Could not get subscriber count", e);
            }
        } else {
           
            return -1;
        }
    }

    @Override
    public long getStreamCount() throws ParsingException {
        Element metaEl = el.select("ul[class*=\"yt-lockup-meta-info\"]").first();
        if (metaEl == null) {
            return 0;
        } else {
            return Long.parseLong(Utils.removeNonDigitCharacters(metaEl.text()));
        }
    }

    @Override
    public String getDescription() throws ParsingException {
        Element desEl = el.select("div[class*=\"yt-lockup-description\"]").first();
        if (desEl == null) {
            return "";
        } else {
            return desEl.text();
        }
    }
}
