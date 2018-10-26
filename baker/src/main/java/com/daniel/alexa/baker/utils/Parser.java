package com.daniel.alexa.baker.utils;

import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;
import com.daniel.alexa.baker.exceptions.ParsingException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {

    private Parser() {
    }

    public static class RegexException extends ParsingException {
        public RegexException(String message) {
            super(message);
        }
    }

    public static String matchGroup1(String pattern, String input) throws RegexException {
        return matchGroup(pattern, input, 1);
    }

    public static String matchGroup(String pattern, String input, int group) throws RegexException {
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(input);
        boolean foundMatch = mat.find();
        if (foundMatch) {
            return mat.group(group);
        } else {
            if (input.length() > 1024) {
                throw new RegexException("failed to find pattern \"" + pattern);
            } else {
                throw new RegexException("failed to find pattern \"" + pattern + " inside of " + input + "\"");
            }
        }
    }

    public static boolean isMatch(String pattern, String input) {
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(input);
        return mat.find();
    }

    public static Map<String, String> compatParseMap(final String input) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        for (String arg : input.split("&")) {
            String[] splitArg = arg.split("=");
            if (splitArg.length > 1) {
                map.put(splitArg[0], URLDecoder.decode(splitArg[1], "UTF-8"));
            } else {
                map.put(splitArg[0], "");
            }
        }
        return map;
    }

    public static String[] getLinksFromString(final String txt) throws ParsingException {
        try {
            ArrayList<String> links = new ArrayList<>();
            LinkExtractor linkBaker = LinkExtractor.builder()
                    .linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW))
                    .build();
            Iterable<LinkSpan> linkss = linkBaker.extractLinks(txt);
            for(LinkSpan ls : linkss) {
                links.add(txt.substring(ls.getBeginIndex(), ls.getEndIndex()));
            }

            String[] linksarray = new String[links.size()];
            linksarray = links.toArray(linksarray);
            return linksarray;
        } catch (Exception e) {
            throw new ParsingException("Could not get links from string", e);
        }
    }
}
