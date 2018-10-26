package com.daniel.alexa.baker.services;

import com.daniel.alexa.baker.InfoItem;
import com.daniel.alexa.baker.ListBaker;
import com.daniel.alexa.baker.stream.StreamInfoItem;

import java.util.List;

import static org.junit.Assert.*;
import static com.daniel.alexa.baker.BakerAsserts.*;

public final class DefaultTests {
    public static void defaultTestListOfItems(int expectedServiceId, List<? extends InfoItem> itemsList, List<Throwable> errors) {
        assertTrue("List of items is empty", !itemsList.isEmpty());
        assertFalse("List of items contains a null element", itemsList.contains(null));
        assertEmptyErrors("Errors during stream list extraction", errors);

        for (InfoItem item : itemsList) {
            assertIsSecureUrl(item.getUrl());
            if (item.getThumbnailUrl() != null && !item.getThumbnailUrl().isEmpty()) {
                assertIsSecureUrl(item.getThumbnailUrl());
            }
            assertNotNull("InfoItem type not set: " + item, item.getInfoType());
            assertEquals("Service id doesn't match: " + item, expectedServiceId, item.getServiceId());

            if (item instanceof StreamInfoItem) {
                StreamInfoItem streamInfoItem = (StreamInfoItem) item;
                assertNotEmpty("Uploader name not set: " + item, streamInfoItem.getUploaderName());
                assertNotEmpty("Uploader url not set: " + item, streamInfoItem.getUploaderUrl());
            }
        }
    }

    public static <T extends InfoItem> ListBaker.InfoItemsPage<T> defaultTestRelatedItems(ListBaker<T> baker, int expectedServiceId) throws Exception {
        final ListBaker.InfoItemsPage<T> page = baker.getInitialPage();
        final List<T> itemsList = page.getItems();
        List<Throwable> errors = page.getErrors();

        defaultTestListOfItems(expectedServiceId, itemsList, errors);
        return page;
    }

    public static <T extends InfoItem> ListBaker.InfoItemsPage<T> defaultTestMoreItems(ListBaker<T> baker, int expectedServiceId) throws Exception {
        assertTrue("Doesn't have more items", baker.hasNextPage());
        ListBaker.InfoItemsPage<T> nextPage = baker.getPage(baker.getNextPageUrl());
        final List<T> items = nextPage.getItems();
        assertTrue("Next page is empty", !items.isEmpty());
        assertEmptyErrors("Next page have errors", nextPage.getErrors());

        defaultTestListOfItems(expectedServiceId, nextPage.getItems(), nextPage.getErrors());
        return nextPage;
    }

    public static void defaultTestGetPageInNewBaker(ListBaker<? extends InfoItem> baker, ListBaker<? extends InfoItem> newBaker, int expectedServiceId) throws Exception {
        final String nextPageUrl = baker.getNextPageUrl();

        final ListBaker.InfoItemsPage<? extends InfoItem> page = newBaker.getPage(nextPageUrl);
        defaultTestListOfItems(expectedServiceId, page.getItems(), page.getErrors());
    }
}
