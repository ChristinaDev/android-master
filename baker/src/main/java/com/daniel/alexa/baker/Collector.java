package com.daniel.alexa.baker;

import com.daniel.alexa.baker.exceptions.ParsingException;

import java.util.List;

/**
 * Collectors are used to simplify the collection of information
 * from bakers
 * @param <I> the item type
 * @param <E> the baker type
 */
public interface Collector<I, E> {

    /**
     * Try to add an baker to the collection
     * @param baker the baker to add
     */
    void commit(E baker);

    /**
     * Try to extract the item from an baker without adding it to the collection
     * @param baker the baker to use
     * @return the item
     * @throws ParsingException thrown if there is an error extracting the
     *                          <b>required</b> fields of the item.
     */
    I extract(E baker) throws ParsingException;

    /**
     * Get all items
     * @return the items
     */
    List<I> getItems();

    /**
     * Get all errors
     * @return the errors
     */
    List<Throwable> getErrors();

    /**
     * Reset all collected items and errors
     */
    void reset();
}
