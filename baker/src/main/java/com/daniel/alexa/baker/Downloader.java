package com.daniel.alexa.baker;

import com.daniel.alexa.baker.exceptions.ReCaptchaException;

import java.io.IOException;
import java.util.Map;


public interface Downloader {

    /**
     * Download the text file at the supplied URL as in download(String),
     * but set the HTTP header field "Accept-Language" to the supplied string.
     *
     * @param siteUrl  the URL of the text file to return the contents of
     * @param language the language (usually a 2-character code) to set as the preferred language
     * @return the contents of the specified text file
     * @throws IOException
     */
    String download(String siteUrl, String language) throws IOException, ReCaptchaException;

    /**
     * Download the text file at the supplied URL as in download(String),
     * but set the HTTP header field "Accept-Language" to the supplied string.
     *
     * @param siteUrl          the URL of the text file to return the contents of
     * @param customProperties set request header properties
     * @return the contents of the specified text file
     * @throws IOException
     */
    String download(String siteUrl, Map<String, String> customProperties) throws IOException, ReCaptchaException;

    /**
     * Download (via HTTP) the text file located at the supplied URL, and return its contents.
     * Primarily intended for downloading web pages.
     *
     * @param siteUrl the URL of the text file to download
     * @return the contents of the specified text file
     * @throws IOException
     */
    String download(String siteUrl) throws IOException, ReCaptchaException;
}
