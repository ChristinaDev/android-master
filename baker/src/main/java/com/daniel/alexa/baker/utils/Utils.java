package com.daniel.alexa.baker.utils;

import com.daniel.alexa.baker.exceptions.ParsingException;

import java.util.List;

public class Utils {

    private Utils() {
        //no instance
    }

    /**
     * @param toRemove string to remove non-digit chars
     * @return a string that contains only digits
     */
    public static String removeNonDigitCharacters(String toRemove) {
        return toRemove.replaceAll("\\D+", "");
    }

    /**
     * @param pattern the pattern that will be used to check the url
     * @param url     the url to be tested
     */
    public static void checkUrl(String pattern, String url) throws ParsingException {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Url can't be null or empty");
        }

        if (!Parser.isMatch(pattern, url.toLowerCase())) {
            throw new ParsingException("Url don't match the pattern");
        }
    }

    public static void printErrors(List<Throwable> errors) {
        for(Throwable e : errors) {
            e.printStackTrace();
            System.err.println("----------------");
        }
    }

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    public static String replaceHttpWithHttps(final String url) {
        if (url == null) return null;

        if(!url.isEmpty() && url.startsWith(HTTP)) {
            return HTTPS + url.substring(HTTP.length());
        }
        return url;
    }
}

