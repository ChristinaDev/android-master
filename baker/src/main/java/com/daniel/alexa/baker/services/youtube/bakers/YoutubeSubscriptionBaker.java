package com.daniel.alexa.baker.services.youtube.bakers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.daniel.alexa.baker.exceptions.ExtractionException;
import com.daniel.alexa.baker.services.youtube.YoutubeService;
import com.daniel.alexa.baker.subscription.SubscriptionBaker;
import com.daniel.alexa.baker.subscription.SubscriptionItem;
import com.daniel.alexa.baker.utils.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.daniel.alexa.baker.subscription.SubscriptionBaker.ContentSource.INPUT_STREAM;

public class YoutubeSubscriptionBaker extends SubscriptionBaker {

    public YoutubeSubscriptionBaker(YoutubeService service) {
        super(service, Collections.singletonList(INPUT_STREAM));
    }

    @Override
    public String getRelatedUrl() {
        return "https://www.youtube.com/subscription_manager?action_takeout=1";
    }

    @Override
    public List<SubscriptionItem> fromInputStream(InputStream contentInputStream) throws ExtractionException {
        if (contentInputStream == null) throw new InvalidSourceException("input stream is null");

        return getItemsFromOPML(contentInputStream);
    }

 

    private static final String ID_PATTERN = "/videos.xml\\?channel_id=([A-Za-z0-9_-]*)";
    private static final String BASE_CHANNEL_URL = "https://www.youtube.com/channel/";

    private List<SubscriptionItem> getItemsFromOPML(InputStream contentInputStream) throws ExtractionException {
        final List<SubscriptionItem> result = new ArrayList<>();

        final String contentString = readFromInputStream(contentInputStream);
        Document document = Jsoup.parse(contentString, "", org.jsoup.parser.Parser.xmlParser());

        if (document.select("opml").isEmpty()) {
            throw new InvalidSourceException("document does not have OPML tag");
        }

        if (document.select("outline").isEmpty()) {
            throw new InvalidSourceException("document does not have at least one outline tag");
        }

        for (Element outline : document.select("outline[type=rss]")) {
            String title = outline.attr("title");
            String xmlUrl = outline.attr("abs:xmlUrl");

            if (title.isEmpty() || xmlUrl.isEmpty()) {
                throw new InvalidSourceException("document has invalid entries");
            }

            try {
                String id = Parser.matchGroup1(ID_PATTERN, xmlUrl);
                result.add(new SubscriptionItem(service.getServiceId(), BASE_CHANNEL_URL + id, title));
            } catch (Parser.RegexException e) {
                throw new InvalidSourceException("document has invalid entries", e);
            }
        }

        return result;
    }


    private void throwIfTagIsNotFound(String content) throws InvalidSourceException {
        if (!content.trim().contains("<opml")) {
            throw new InvalidSourceException("input stream does not have OPML tag");
        }
    }

    private String readFromInputStream(InputStream inputStream) throws InvalidSourceException {
        StringBuilder contentBuilder = new StringBuilder();
        boolean hasTag = false;
        try {
            byte[] buffer = new byte[16 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                String currentPartOfContent = new String(buffer, 0, read, "UTF-8");
                contentBuilder.append(currentPartOfContent);

          
                if (!hasTag && contentBuilder.length() > 128) {
                    throwIfTagIsNotFound(contentBuilder.toString());
                    hasTag = true;
                }
            }
        } catch (InvalidSourceException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvalidSourceException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }

        final String fileContent = contentBuilder.toString().trim();
        if (fileContent.isEmpty()) {
            throw new InvalidSourceException("Empty input stream");
        }

        if (!hasTag) {
            throwIfTagIsNotFound(fileContent);
        }

        return fileContent;
    }
}
