package com.daniel.alexa.baker.services;

@SuppressWarnings("unused")
public interface BaseChannelBakerTest extends BaseListBakerTest {
    void testDescription() throws Exception;
    void testAvatarUrl() throws Exception;
    void testBannerUrl() throws Exception;
    void testFeedUrl() throws Exception;
    void testSubscriberCount() throws Exception;
}
