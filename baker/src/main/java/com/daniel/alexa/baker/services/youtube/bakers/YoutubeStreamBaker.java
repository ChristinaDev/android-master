package com.daniel.alexa.baker.services.youtube.bakers;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import com.daniel.alexa.baker.*;
import com.daniel.alexa.baker.exceptions.ContentNotAvailableException;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.exceptions.ReCaptchaException;
import com.daniel.alexa.baker.linkhandler.LinkHandler;
import com.daniel.alexa.baker.services.youtube.ItagItem;
import com.daniel.alexa.baker.stream.*;
import com.daniel.alexa.baker.utils.Parser;
import com.daniel.alexa.baker.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;



public class YoutubeStreamBaker extends StreamBaker {
    private static final String TAG = YoutubeStreamBaker.class.getSimpleName();


    public class DecryptException extends ParsingException {
        DecryptException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public class GemaException extends ContentNotAvailableException {
        GemaException(String message) {
            super(message);
        }
    }

    public class SubtitlesException extends ContentNotAvailableException {
        SubtitlesException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /*//////////////////////////////////////////////////////////////////////////*/

    private Document doc;
    @Nullable
    private JsonObject playerArgs;
    @Nonnull
    private final Map<String, String> videoInfoPage = new HashMap<>();

    @Nonnull
    private List<SubtitlesInfo> subtitlesInfos = new ArrayList<>();

    private boolean isAgeRestricted;

    public YoutubeStreamBaker(StreamingService service, LinkHandler linkHandler) {
        super(service, linkHandler);
    }



    @Nonnull
    @Override
    public String getName() throws ParsingException {
        assertPageFetched();
        String name = getStringFromMetaData("title");
        if(name == null) {
            // Fallback to HTML method
            try {
                name = doc.select("meta[name=title]").attr(CONTENT);
            } catch (Exception e) {
                throw new ParsingException("Could not get the title", e);
            }
        }
        if(name == null || name.isEmpty()) {
            throw new ParsingException("Could not get the title");
        }
        return name;
    }

    @Nonnull
    @Override
    public String getUploadDate() throws ParsingException {
        assertPageFetched();
        try {
            return doc.select("meta[itemprop=datePublished]").attr(CONTENT);
        } catch (Exception e) {//todo: add fallback method
            throw new ParsingException("Could not get upload date", e);
        }
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        assertPageFetched();
     
        try {
            return doc.select("link[itemprop=\"thumbnailUrl\"]").first().attr("abs:href");
        } catch (Exception ignored) {
            // Try other method...
        }

        try {
            if (playerArgs != null && playerArgs.isString("thumbnail_url")) return playerArgs.getString("thumbnail_url");
        } catch (Exception ignored) {
            // Try other method...
        }

        try {
            return videoInfoPage.get("thumbnail_url");
        } catch (Exception e) {
            throw new ParsingException("Could not get thumbnail url", e);
        }
    }

    @Nonnull
    @Override
    public String getDescription() throws ParsingException {
        assertPageFetched();
        try {
            return parseHtmlAndGetFullLinks(doc.select("p[id=\"eow-description\"]").first().html());
        } catch (Exception e) {
            throw new ParsingException("Could not get the description", e);
        }
    }

    private String parseHtmlAndGetFullLinks(String descriptionHtml)
            throws MalformedURLException, UnsupportedEncodingException, ParsingException {
        final Document description = Jsoup.parse(descriptionHtml, getUrl());
        for(Element a : description.select("a")) {
            final URL redirectLink = new URL(
                    a.attr("abs:href"));
            final String queryString = redirectLink.getQuery();
            if(queryString != null) {
                // if the query string is null we are not dealing with a redirect link,
                // so we don't need to override it.
                final String link =
                        Parser.compatParseMap(queryString).get("q");

                if(link != null) {
                    // if link is null the a tag is a hashtag.
                    // They refer to the youtube search. We do not handle them.
                    a.text(link);

                }
            } else if(redirectLink.toString().contains("watch?v=")
                    || redirectLink.toString().contains("https://www.youtube.com/")) {
                // Another posibility is that this link is pointing to another video
                // we need to put the redirectLink in here explicitly in order to add the domain part to the link.
                a.text(redirectLink.toString());
            }
        }
        return description.select("body").first().html();
    }

    @Override
    public int getAgeLimit() throws ParsingException {
        assertPageFetched();
        if (!isAgeRestricted) {
            return NO_AGE_LIMIT;
        }
        try {
            return Integer.valueOf(doc.select("meta[property=\"og:restrictions:age\"]")
                    .attr(CONTENT).replace("+", ""));
        } catch (Exception e) {
            throw new ParsingException("Could not get age restriction");
        }
    }

    @Override
    public long getLength() throws ParsingException {
        assertPageFetched();
        if(playerArgs != null) {
            try {
                long returnValue = Long.parseLong(playerArgs.get("length_seconds") + "");
                if (returnValue >= 0) return returnValue;
            } catch (Exception ignored) {
                // Try other method...
            }
        }

        String lengthString = videoInfoPage.get("length_seconds");
        try {
            return Long.parseLong(lengthString);
        } catch (Exception ignored) {
            // Try other method...
        }

        try {
            // Fallback to HTML method
            return Long.parseLong(doc.select("div[class~=\"ytp-progress-bar\"][role=\"slider\"]").first()
                    .attr("aria-valuemax"));
        } catch (Exception e) {
            throw new ParsingException("Could not get video length", e);
        }
    }

    /**
     * Attempts to parse (and return) the offset to start playing the video from.
     *
     * @return the offset (in seconds), or 0 if no timestamp is found.
     */
    @Override
    public long getTimeStamp() throws ParsingException {
        return getTimestampSeconds("((#|&|\\?)t=\\d{0,3}h?\\d{0,3}m?\\d{1,3}s?)");
    }

    @Override
    public long getViewCount() throws ParsingException {
        assertPageFetched();
        try {
            return Long.parseLong(doc.select("meta[itemprop=interactionCount]").attr(CONTENT));
        } catch (Exception e) {//todo: find fallback method
            throw new ParsingException("Could not get number of views", e);
        }
    }

    @Override
    public long getLikeCount() throws ParsingException {
        assertPageFetched();
        String likesString = "";
        try {
            Element button = doc.select("button.like-button-renderer-like-button").first();
            try {
                likesString = button.select("span.yt-uix-button-content").first().text();
            } catch (NullPointerException e) {
            
                return -1;
            }
            return Integer.parseInt(Utils.removeNonDigitCharacters(likesString));
        } catch (NumberFormatException nfe) {
            throw new ParsingException("Could not parse \"" + likesString + "\" as an Integer", nfe);
        } catch (Exception e) {
            throw new ParsingException("Could not get like count", e);
        }
    }

    @Override
    public long getDislikeCount() throws ParsingException {
        assertPageFetched();
        String dislikesString = "";
        try {
            Element button = doc.select("button.like-button-renderer-dislike-button").first();
            try {
                dislikesString = button.select("span.yt-uix-button-content").first().text();
            } catch (NullPointerException e) {
               
                return -1;
            }
            return Integer.parseInt(Utils.removeNonDigitCharacters(dislikesString));
        } catch (NumberFormatException nfe) {
            throw new ParsingException("Could not parse \"" + dislikesString + "\" as an Integer", nfe);
        } catch (Exception e) {
            throw new ParsingException("Could not get dislike count", e);
        }
    }

    @Nonnull
    @Override
    public String getUploaderUrl() throws ParsingException {
        assertPageFetched();
        try {
            return doc.select("div[class=\"yt-user-info\"]").first().children()
                    .select("a").first().attr("abs:href");
        } catch (Exception e) {
            throw new ParsingException("Could not get channel link", e);
        }
    }


    @Nullable
    private String getStringFromMetaData(String field) {
        assertPageFetched();
        String value = null;
        if(playerArgs != null) {
            // This can not fail
            value = playerArgs.getString(field);
        }
        if(value == null) {
            // This can not fail too
            value = videoInfoPage.get(field);
        }
        return value;
    }

    @Nonnull
    @Override
    public String getUploaderName() throws ParsingException {
        assertPageFetched();
        String name = getStringFromMetaData("author");

        if(name == null) {
            try {
                // Fallback to HTML method
                name = doc.select("div.yt-user-info").first().text();
            } catch (Exception e) {
                throw new ParsingException("Could not get uploader name", e);
            }
        }
        if(name == null || name.isEmpty()) {
            throw new ParsingException("Could not get uploader name");
        }
        return name;
    }

    @Nonnull
    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        assertPageFetched();
        try {
            return doc.select("a[class*=\"yt-user-photo\"]").first()
                    .select("img").first()
                    .attr("abs:data-thumb");
        } catch (Exception e) {//todo: add fallback method
            throw new ParsingException("Could not get uploader thumbnail URL.", e);
        }
    }

    @Nonnull
    @Override
    public String getDashMpdUrl() throws ParsingException {
        assertPageFetched();
        try {
            String dashManifestUrl;
            if (videoInfoPage.containsKey("dashmpd")) {
                dashManifestUrl = videoInfoPage.get("dashmpd");
            } else if (playerArgs != null && playerArgs.isString("dashmpd")) {
                dashManifestUrl = playerArgs.getString("dashmpd", "");
            } else {
                return "";
            }

            if (!dashManifestUrl.contains("/signature/")) {
                String encryptedSig = Parser.matchGroup1("/s/([a-fA-F0-9\\.]+)", dashManifestUrl);
                String decryptedSig;

                decryptedSig = decryptSignature(encryptedSig, decryptionCode);
                dashManifestUrl = dashManifestUrl.replace("/s/" + encryptedSig, "/signature/" + decryptedSig);
            }

            return dashManifestUrl;
        } catch (Exception e) {
            throw new ParsingException("Could not get dash manifest url", e);
        }
    }

    @Nonnull
    @Override
    public String getHlsUrl() throws ParsingException {
        assertPageFetched();
        try {
            String hlsvp;
            if (playerArgs != null && playerArgs.isString("hlsvp")) {
                hlsvp = playerArgs.getString("hlsvp", "");
            } else {
                return "";
            }

            return hlsvp;
        } catch (Exception e) {
            throw new ParsingException("Could not get hls manifest url", e);
        }
    }

    @Override
    public List<AudioStream> getAudioStreams() throws IOException, ExtractionException {
        assertPageFetched();
        List<AudioStream> audioStreams = new ArrayList<>();
        try {
            for (Map.Entry<String, ItagItem> entry : getItags(ADAPTIVE_FMTS, ItagItem.ItagType.AUDIO).entrySet()) {
                ItagItem itag = entry.getValue();

                AudioStream audioStream = new AudioStream(entry.getKey(), itag.getMediaFormat(), itag.avgBitrate);
                if (!Stream.containSimilarStream(audioStream, audioStreams)) {
                    audioStreams.add(audioStream);
                }
            }
        } catch (Exception e) {
            throw new ParsingException("Could not get audio streams", e);
        }

        return audioStreams;
    }

    @Override
    public List<VideoStream> getVideoStreams() throws IOException, ExtractionException {
        assertPageFetched();
        List<VideoStream> videoStreams = new ArrayList<>();
        try {
            for (Map.Entry<String, ItagItem> entry : getItags(URL_ENCODED_FMT_STREAM_MAP, ItagItem.ItagType.VIDEO).entrySet()) {
                ItagItem itag = entry.getValue();

                VideoStream videoStream = new VideoStream(entry.getKey(), itag.getMediaFormat(), itag.resolutionString);
                if (!Stream.containSimilarStream(videoStream, videoStreams)) {
                    videoStreams.add(videoStream);
                }
            }
        } catch (Exception e) {
            throw new ParsingException("Could not get video streams", e);
        }

        return videoStreams;
    }

    @Override
    public List<VideoStream> getVideoOnlyStreams() throws ExtractionException {
        assertPageFetched();
        List<VideoStream> videoOnlyStreams = new ArrayList<>();
        try {
            for (Map.Entry<String, ItagItem> entry : getItags(ADAPTIVE_FMTS, ItagItem.ItagType.VIDEO_ONLY).entrySet()) {
                ItagItem itag = entry.getValue();

                VideoStream videoStream = new VideoStream(entry.getKey(), itag.getMediaFormat(), itag.resolutionString, true);
                if (!Stream.containSimilarStream(videoStream, videoOnlyStreams)) {
                    videoOnlyStreams.add(videoStream);
                }
            }
        } catch (Exception e) {
            throw new ParsingException("Could not get video only streams", e);
        }

        return videoOnlyStreams;
    }

    @Override
    @Nonnull
    public List<Subtitles> getSubtitlesDefault() throws IOException, ExtractionException {
        return getSubtitles(SubtitlesFormat.TTML);
    }

    @Override
    @Nonnull
    public List<Subtitles> getSubtitles(final SubtitlesFormat format) throws IOException, ExtractionException {
        assertPageFetched();
        List<Subtitles> subtitles = new ArrayList<>();
        for (final SubtitlesInfo subtitlesInfo : subtitlesInfos) {
            subtitles.add(subtitlesInfo.getSubtitle(format));
        }
        return subtitles;
    }

    @Override
    public StreamType getStreamType() throws ParsingException {
        assertPageFetched();
        try {
            if (playerArgs != null && (playerArgs.has("ps") && playerArgs.get("ps").toString().equals("live") ||
                    playerArgs.get(URL_ENCODED_FMT_STREAM_MAP).toString().isEmpty())) {
                return StreamType.LIVE_STREAM;
            }
        } catch (Exception e) {
            throw new ParsingException("Could not get hls manifest url", e);
        }
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public StreamInfoItem getNextVideo() throws IOException, ExtractionException {
        assertPageFetched();
        try {
            StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
            collector.commit(extractVideoPreviewInfo(doc.select("div[class=\"watch-sidebar-section\"]")
                    .first().select("li").first()));

            return collector.getItems().get(0);
        } catch (Exception e) {
            throw new ParsingException("Could not get next video", e);
        }
    }

    @Override
    public StreamInfoItemsCollector getRelatedVideos() throws IOException, ExtractionException {
        assertPageFetched();
        try {
            StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
            Element ul = doc.select("ul[id=\"watch-related\"]").first();
            if (ul != null) {
                for (Element li : ul.children()) {
                    // first check if we have a playlist. If so leave them out
                    if (li.select("a[class*=\"content-link\"]").first() != null) {
                        collector.commit(extractVideoPreviewInfo(li));
                    }
                }
            }
            return collector;
        } catch (Exception e) {
            throw new ParsingException("Could not get related videos", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorMessage() {
        String errorMessage = doc.select("h1[id=\"unavailable-message\"]").first().text();
        StringBuilder errorReason;

        if (errorMessage == null || errorMessage.isEmpty()) {
            errorReason = null;
        } else if (errorMessage.contains("GEMA")) {
        
            errorReason = new StringBuilder("GEMA");
        } else {
            errorReason = new StringBuilder(errorMessage);
            errorReason.append("  ");
            errorReason.append(doc.select("[id=\"unavailable-submessage\"]").first().text());
        }

        return errorReason != null ? errorReason.toString() : null;
    }


    private static final String URL_ENCODED_FMT_STREAM_MAP = "url_encoded_fmt_stream_map";
    private static final String ADAPTIVE_FMTS = "adaptive_fmts";
    private static final String HTTPS = "https:";
    private static final String CONTENT = "content";
    private static final String DECRYPTION_FUNC_NAME = "decrypt";

    private static final String VERIFIED_URL_PARAMS = "&has_verified=1&bpctr=9999999999";

    private final static String DECYRYPTION_SIGNATURE_FUNCTION_REGEX =
            "(\\w+)\\s*=\\s*function\\((\\w+)\\)\\{\\s*\\2=\\s*\\2\\.split\\(\"\"\\)\\s*;";
    private final static String DECRYPTION_AKAMAIZED_STRING_REGEX =
            "yt\\.akamaized\\.net/\\)\\s*\\|\\|\\s*.*?\\s*c\\s*&&\\s*d\\.set\\([^,]+\\s*,\\s*([a-zA-Z0-9$]+)\\(";
    private final static String DECRYPTION_AKAMAIZED_SHORT_STRING_REGEX =
            "\\bc\\s*&&\\s*d\\.set\\([^,]+\\s*,\\s*([a-zA-Z0-9$]+)\\(";

    private volatile String decryptionCode = "";

    private String pageHtml = null;

    private String getPageHtml(Downloader downloader) throws IOException, ExtractionException {
        final String verifiedUrl = getUrl() + VERIFIED_URL_PARAMS;
        if (pageHtml == null) {
            pageHtml = downloader.download(verifiedUrl);
        }
        return pageHtml;
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        final String pageContent = getPageHtml(downloader);
        doc = Jsoup.parse(pageContent, getUrl());

        final String playerUrl;
        // Check if the video is age restricted
        if (pageContent.contains("<meta property=\"og:restrictions:age")) {
            final EmbeddedInfo info = getEmbeddedInfo();
            final String videoInfoUrl = getVideoInfoUrl(getId(), info.sts);
            final String infoPageResponse = downloader.download(videoInfoUrl);
            videoInfoPage.putAll(Parser.compatParseMap(infoPageResponse));
            playerUrl = info.url;
            isAgeRestricted = true;
        } else {
            final JsonObject ytPlayerConfig = getPlayerConfig(pageContent);
            playerArgs = getPlayerArgs(ytPlayerConfig);
            playerUrl = getPlayerUrl(ytPlayerConfig);
            isAgeRestricted = false;
        }

        if (decryptionCode.isEmpty()) {
            decryptionCode = loadDecryptionCode(playerUrl);
        }

        if (subtitlesInfos.isEmpty()) {
            subtitlesInfos.addAll(getAvailableSubtitlesInfo());
        }
    }

    private JsonObject getPlayerConfig(String pageContent) throws ParsingException {
        try {
            String ytPlayerConfigRaw = Parser.matchGroup1("ytplayer.config\\s*=\\s*(\\{.*?\\});", pageContent);
            return JsonParser.object().from(ytPlayerConfigRaw);
        } catch (Parser.RegexException e) {
            String errorReason = getErrorMessage();
            switch (errorReason) {
                case "GEMA":
                    throw new GemaException(errorReason);
                case "":
                    throw new ContentNotAvailableException("Content not available: player config empty", e);
                default:
                    throw new ContentNotAvailableException("Content not available", e);
            }
        } catch (Exception e) {
            throw new ParsingException("Could not parse yt player config", e);
        }
    }

    private JsonObject getPlayerArgs(JsonObject playerConfig) throws ParsingException {
        JsonObject playerArgs;

     
        try {
            playerArgs = playerConfig.getObject("args");
        } catch (Exception e) {
            throw new ParsingException("Could not parse yt player config", e);
        }

        return playerArgs;
    }

    private String getPlayerUrl(JsonObject playerConfig) throws ParsingException {
        try {
         
            String playerUrl;

            JsonObject ytAssets = playerConfig.getObject("assets");
            playerUrl = ytAssets.getString("js");

            if (playerUrl.startsWith("//")) {
                playerUrl = HTTPS + playerUrl;
            }
            return playerUrl;
        } catch (Exception e) {
            throw new ParsingException("Could not load decryption code for the Youtube service.", e);
        }
    }

    @Nonnull
    private EmbeddedInfo getEmbeddedInfo() throws ParsingException, ReCaptchaException {
        try {
            final Downloader downloader = Alexa.getDownloader();
            final String embedUrl = "https://www.youtube.com/embed/" + getId();
            final String embedPageContent = downloader.download(embedUrl);

            // Get player url
            final String assetsPattern = "\"assets\":.+?\"js\":\\s*(\"[^\"]+\")";
            String playerUrl = Parser.matchGroup1(assetsPattern, embedPageContent)
                    .replace("\\", "").replace("\"", "");
            if (playerUrl.startsWith("//")) {
                playerUrl = HTTPS + playerUrl;
            }

            // Get embed sts
            final String stsPattern = "\"sts\"\\s*:\\s*(\\d+)";
            final String sts = Parser.matchGroup1(stsPattern, embedPageContent);

            return new EmbeddedInfo(playerUrl, sts);
        } catch (IOException e) {
            throw new ParsingException(
                    "Could load decryption code form restricted video for the Youtube service.", e);
        } catch (ReCaptchaException e) {
            throw new ReCaptchaException("reCaptcha Challenge requested");
        }
    }

    private String loadDecryptionCode(String playerUrl) throws DecryptException {
        try {
            Downloader downloader = Alexa.getDownloader();
            if (!playerUrl.contains("https://youtube.com")) {
          
                playerUrl = "https://youtube.com" + playerUrl;
            }

            final String playerCode = downloader.download(playerUrl);

            final String decryptionFunctionName;
            if (Parser.isMatch(DECRYPTION_AKAMAIZED_SHORT_STRING_REGEX, playerCode)) {
                decryptionFunctionName = Parser.matchGroup1(DECRYPTION_AKAMAIZED_SHORT_STRING_REGEX, playerCode);
            } else if (Parser.isMatch(DECRYPTION_AKAMAIZED_STRING_REGEX, playerCode)) {
                decryptionFunctionName = Parser.matchGroup1(DECRYPTION_AKAMAIZED_STRING_REGEX, playerCode);
            } else {
                decryptionFunctionName = Parser.matchGroup1(DECYRYPTION_SIGNATURE_FUNCTION_REGEX, playerCode);
            }
            final String functionPattern = "("
                    + decryptionFunctionName.replace("$", "\\$")
                    + "=function\\([a-zA-Z0-9_]+\\)\\{.+?\\})";
            final String decryptionFunction = "var " + Parser.matchGroup1(functionPattern, playerCode) + ";";

            final String helperObjectName =
                    Parser.matchGroup1(";([A-Za-z0-9_\\$]{2})\\...\\(", decryptionFunction);
            final String helperPattern =
                    "(var " + helperObjectName.replace("$", "\\$") + "=\\{.+?\\}\\};)";
            final String helperObject =
                    Parser.matchGroup1(helperPattern, playerCode.replace("\n", ""));

            final String callerFunction =
                    "function " + DECRYPTION_FUNC_NAME + "(a){return " + decryptionFunctionName + "(a);}";

            return helperObject + decryptionFunction + callerFunction;
        } catch (IOException ioe) {
            throw new DecryptException("Could not load decrypt function", ioe);
        } catch (Exception e) {
            throw new DecryptException("Could not parse decrypt function ", e);
        }
    }

    private String decryptSignature(String encryptedSig, String decryptionCode) throws DecryptException {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        Object result;
        try {
            ScriptableObject scope = context.initStandardObjects();
            context.evaluateString(scope, decryptionCode, "decryptionCode", 1, null);
            Function decryptionFunc = (Function) scope.get("decrypt", scope);
            result = decryptionFunc.call(context, scope, scope, new Object[]{encryptedSig});
        } catch (Exception e) {
            throw new DecryptException("could not get decrypt signature", e);
        } finally {
            Context.exit();
        }
        return result == null ? "" : result.toString();
    }

    @Nonnull
    private List<SubtitlesInfo> getAvailableSubtitlesInfo() throws SubtitlesException {
        // If the video is age restricted getPlayerConfig will fail
        if(isAgeRestricted) return Collections.emptyList();

        final JsonObject playerConfig;
        try {
            playerConfig = getPlayerConfig(getPageHtml(Alexa.getDownloader()));
        } catch (IOException | ExtractionException e) {
            throw new SubtitlesException("Unable to download player configs", e);
        }
        final String playerResponse = playerConfig.getObject("args", new JsonObject())
                .getString("player_response");

        final JsonObject captions;
        try {
            if (playerResponse == null || !JsonParser.object().from(playerResponse).has("captions")) {
                // Captions does not exist
                return Collections.emptyList();
            }
            captions = JsonParser.object().from(playerResponse).getObject("captions");
        } catch (JsonParserException e) {
            throw new SubtitlesException("Unable to parse subtitles listing", e);
        }

        final JsonObject renderer = captions.getObject("playerCaptionsTracklistRenderer", new JsonObject());
        final JsonArray captionsArray = renderer.getArray("captionTracks", new JsonArray());
        // todo: use this to apply auto translation to different language from a source language
        final JsonArray autoCaptionsArray = renderer.getArray("translationLanguages", new JsonArray());

        final int captionsSize = captionsArray.size();
        if(captionsSize == 0) return Collections.emptyList();

        List<SubtitlesInfo> result = new ArrayList<>();
        for (int i = 0; i < captionsSize; i++) {
            final String languageCode = captionsArray.getObject(i).getString("languageCode");
            final String baseUrl = captionsArray.getObject(i).getString("baseUrl");
            final String vssId = captionsArray.getObject(i).getString("vssId");

            if (languageCode != null && baseUrl != null && vssId != null) {
                final boolean isAutoGenerated = vssId.startsWith("a.");
                result.add(new SubtitlesInfo(baseUrl, languageCode, isAutoGenerated));
            }
        }

        return result;
    }


    private class EmbeddedInfo {
        final String url;
        final String sts;

        EmbeddedInfo(final String url, final String sts) {
            this.url = url;
            this.sts = sts;
        }
    }

    private class SubtitlesInfo {
        final String cleanUrl;
        final String languageCode;
        final boolean isGenerated;

        final Locale locale;

        public SubtitlesInfo(final String baseUrl, final String languageCode, final boolean isGenerated) {
            this.cleanUrl = baseUrl
                    .replaceAll("&fmt=[^&]*", "") // Remove preexisting format if exists
                    .replaceAll("&tlang=[^&]*", ""); // Remove translation language
            this.languageCode = languageCode;
            this.isGenerated = isGenerated;

            final String[] splits = languageCode.split("-");
            this.locale = splits.length == 2 ? new Locale(splits[0], splits[1]) : new Locale(languageCode);
        }

        public Subtitles getSubtitle(final SubtitlesFormat format) {
            return new Subtitles(format, locale, cleanUrl + "&fmt=" + format.getExtension(), isGenerated);
        }
    }


    @Nonnull
    private static String getVideoInfoUrl(final String id, final String sts) {
        return "https://www.youtube.com/get_video_info?" + "video_id=" + id +
                "&eurl=https://youtube.googleapis.com/v/" + id +
                "&sts=" + sts + "&ps=default&gl=US&hl=en";
    }

    private Map<String, ItagItem> getItags(String encodedUrlMapKey, ItagItem.ItagType itagTypeWanted) throws ParsingException {
        Map<String, ItagItem> urlAndItags = new LinkedHashMap<>();

        String encodedUrlMap = "";
        if (playerArgs != null && playerArgs.isString(encodedUrlMapKey)) {
            encodedUrlMap = playerArgs.getString(encodedUrlMapKey, "");
        } else if (videoInfoPage.containsKey(encodedUrlMapKey)) {
            encodedUrlMap = videoInfoPage.get(encodedUrlMapKey);
        }

        for (String url_data_str : encodedUrlMap.split(",")) {
            try {
          
                Map<String, String> tags = Parser.compatParseMap(
                        org.jsoup.parser.Parser.unescapeEntities(url_data_str, true));

                int itag = Integer.parseInt(tags.get("itag"));

                if (ItagItem.isSupported(itag)) {
                    ItagItem itagItem = ItagItem.getItag(itag);
                    if (itagItem.itagType == itagTypeWanted) {
                        String streamUrl = tags.get("url");
               
                        if (tags.get("s") != null) {
                            streamUrl = streamUrl + "&signature=" + decryptSignature(tags.get("s"), decryptionCode);
                        }
                        urlAndItags.put(streamUrl, itagItem);
                    }
                }
            } catch (DecryptException e) {
                throw e;
            } catch (Exception ignored) {
            }
        }

        return urlAndItags;
    }

    private StreamInfoItemBaker extractVideoPreviewInfo(final Element li) {
        return new YoutubeStreamInfoItemBaker(li) {

            @Override
            public String getUrl() throws ParsingException {
                return li.select("a.content-link").first().attr("abs:href");
            }

            @Override
            public String getName() throws ParsingException {
            
                return li.select("span.title").first().text();

            }

            @Override
            public String getUploaderName() throws ParsingException {
                return li.select("span[class*=\"attribution\"").first()
                        .select("span").first().text();
            }

            @Override
            public String getUploaderUrl() throws ParsingException {
                return "";
            }

            @Override
            public String getUploadDate() throws ParsingException {
                return "";
            }

            @Override
            public long getViewCount() throws ParsingException {
                try {
                    if (getStreamType() == StreamType.LIVE_STREAM) return -1;

                    return Long.parseLong(Utils.removeNonDigitCharacters(
                            li.select("span.view-count").first().text()));
                } catch (Exception e) {
                
                    return 0;
                }
            }

            @Override
            public String getThumbnailUrl() throws ParsingException {
                Element img = li.select("img").first();
                String thumbnailUrl = img.attr("abs:src");
         
                if (thumbnailUrl.contains(".gif")) {
                    thumbnailUrl = img.attr("data-thumb");
                }
                if (thumbnailUrl.startsWith("//")) {
                    thumbnailUrl = HTTPS + thumbnailUrl;
                }
                return thumbnailUrl;
            }
        };
    }
}
