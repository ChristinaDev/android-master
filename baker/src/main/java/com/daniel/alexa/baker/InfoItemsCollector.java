package com.daniel.alexa.baker;

import com.daniel.alexa.baker.exceptions.FoundAdException;
import com.daniel.alexa.baker.exceptions.ParsingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public abstract class InfoItemsCollector<I extends InfoItem, E extends InfoItemBaker> implements Collector<I,E> {

    private final List<I> itemList = new ArrayList<>();
    private final List<Throwable> errors = new ArrayList<>();
    private final int serviceId;

    /**
     * Create a new collector
     * @param serviceId the service id
     */
    public InfoItemsCollector(int serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public List<I> getItems() {
        return Collections.unmodifiableList(itemList);
    }

    @Override
    public List<Throwable> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public void reset() {
        itemList.clear();
        errors.clear();
    }

    /**
     * Add an error
     * @param error the error
     */
    protected void addError(Exception error) {
        errors.add(error);
    }

    /**
     * Add an item
     * @param item the item
     */
    protected void addItem(I item) {
        itemList.add(item);
    }

    /**
     * Get the service id
     * @return the service id
     */
    public int getServiceId() {
        return serviceId;
    }

    @Override
    public void commit(E baker) {
        try {
            addItem(extract(baker));
        } catch (FoundAdException ae) {
           
        } catch (ParsingException e) {
            addError(e);
        }
    }
}
