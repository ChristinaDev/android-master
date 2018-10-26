package com.daniel.alexa.baker.services.youtube.bakers;

import org.jsoup.nodes.Element;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.services.youtube.linkHandler.YoutubeParsingHelper;
import com.daniel.alexa.baker.stream.StreamInfoItemBaker;
import com.daniel.alexa.baker.stream.StreamType;
import com.daniel.alexa.baker.utils.Utils;


public class YoutubeStreamInfoItemBaker implements StreamInfoItemBaker {

    private final Element item;

    public YoutubeStreamInfoItemBaker(Element item) {
        this.item = item;
    }

    @Override
    public StreamType getStreamType() throws ParsingException {
        if (isLiveStream(item)) {
            return StreamType.LIVE_STREAM;
        } else {
            return StreamType.VIDEO_STREAM;
        }
    }

    @Override
    public boolean isAd() throws ParsingException {
        return !item.select("span[class*=\"icon-not-available\"]").isEmpty()
                || !item.select("span[class*=\"yt-badge-ad\"]").isEmpty()
                || isPremiumVideo();
    }

    private boolean isPremiumVideo() {
        Element premiumSpan = item.select("span[class=\"standalone-collection-badge-renderer-red-text\"]").first();
        if(premiumSpan == null) return false;

        if(premiumSpan.hasText()) return false;
        return true;
    }

    @Override
    public String getUrl() throws ParsingException {
        try {
            Element el = item.select("div[class*=\"yt-lockup-video\"").first();
            Element dl = el.select("h3").first().select("a").first();
            return dl.attr("abs:href");
        } catch (Exception e) {
            throw new ParsingException("Could not get web page url for the video", e);
        }
    }

    @Override
    public String getName() throws ParsingException {
        try {
            Element el = item.select("div[class*=\"yt-lockup-video\"").first();
            Element dl = el.select("h3").first().select("a").first();
            return dl.text();
        } catch (Exception e) {
            throw new ParsingException("Could not get title", e);
        }
    }

    @Override
    public long getDuration() throws ParsingException {
        try {
            if (getStreamType() == StreamType.LIVE_STREAM) return -1;

            final Element duration = item.select("span[class*=\"video-time\"]").first();

            return duration == null ? 0 : YoutubeParsingHelper.parseDurationString(duration.text());
        } catch (Exception e) {
            throw new ParsingException("Could not get Duration: " + getUrl(), e);
        }
    }

    @Override
    public String getUploaderName() throws ParsingException {
        try {
            return item.select("div[class=\"yt-lockup-byline\"]").first()
                    .select("a").first()
                    .text();
        } catch (Exception e) {
            throw new ParsingException("Could not get uploader", e);
        }
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        try {
            try {
                return item.select("div[class=\"yt-lockup-byline\"]").first()
                        .select("a").first()
                        .attr("abs:href");
            } catch (Exception e){}

   
            return item.select("span[class=\"title\"")
                    .text().split(" - ")[0];
        } catch (Exception e) {
            System.out.println(item.html());
            throw new ParsingException("Could not get uploader", e);
        }
    }

    @Override
    public String getUploadDate() throws ParsingException {
        try {
            Element meta = item.select("div[class=\"yt-lockup-meta\"]").first();
            if (meta == null) return "";

            Element li = meta.select("li").first();
            if(li == null) return "";

            return meta.select("li").first().text();
        } catch (Exception e) {
            throw new ParsingException("Could not get upload date", e);
        }
    }

    @Override
    public long getViewCount() throws ParsingException {
        String input;
        try {
           
            if (getStreamType() == StreamType.LIVE_STREAM) return -1;

            Element meta = item.select("div[class=\"yt-lockup-meta\"]").first();
            if (meta == null) return -1;

       
            if(meta.select("li").size() < 2)  return -1;

            input = meta.select("li").get(1).text();

        } catch (IndexOutOfBoundsException e) {
            throw new ParsingException("Could not parse yt-lockup-meta although available: " + getUrl(), e);
        }

        try {
            return Long.parseLong(Utils.removeNonDigitCharacters(input));
        } catch (NumberFormatException e) {

            if (!input.isEmpty()){
                return 0;
            }

            throw new ParsingException("Could not handle input: " + input, e);
        }
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        try {
            String url;
            Element te = item.select("div[class=\"yt-thumb video-thumb\"]").first()
                    .select("img").first();
            url = te.attr("abs:src");

            if (url.contains(".gif")) {
                url = te.attr("abs:data-thumb");
            }
            return url;
        } catch (Exception e) {
            throw new ParsingException("Could not get thumbnail url", e);
        }
    }

    protected static boolean isLiveStream(Element item) {
        return !item.select("span[class*=\"yt-badge-live\"]").isEmpty()
                || !item.select("span[class*=\"video-time-overlay-live\"]").isEmpty();
    }
}
