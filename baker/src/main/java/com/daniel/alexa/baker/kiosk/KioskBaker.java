package com.daniel.alexa.baker.kiosk;



import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.StreamingService;
import com.daniel.alexa.baker.exceptions.ParsingException;
import com.daniel.alexa.baker.stream.StreamInfoItem;
import com.daniel.alexa.baker.linkhandler.ListLinkHandler;

import javax.annotation.Nonnull;

public abstract class KioskBaker extends ListBaker<StreamInfoItem> {
    private String contentCountry = null;
    private final String id;

    public KioskBaker(StreamingService streamingService,
                          ListLinkHandler urlIdHandler,
                          String kioskId) {
        super(streamingService, urlIdHandler);
        this.id = kioskId;
    }

    /**
     * For certain Websites the content of a kiosk will be different depending
     * on the country you want to poen the website in. Therefore you should
     * set the contentCountry.
     * @param contentCountry 
     */
    public void setContentCountry(String contentCountry) {
        this.contentCountry = contentCountry;
    }


    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    /**
     * Id should be the name of the kiosk, tho Id is used for identifing it in the frontend,
     * so id should be kept in english.
     * In order to get the name of the kiosk in the desired language we have to
     * crawl if from the website.
     * @return the tranlsated version of id
     * @throws ParsingException
     */
    @Nonnull
    @Override
    public abstract String getName() throws ParsingException;


    public String getContentCountry() {
        return contentCountry;
    }
}
