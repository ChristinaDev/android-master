package com.daniel.alexa.baker.services.youtube.linkHandler;


import com.daniel.alexa.baker.exceptions.ParsingException;


public class YoutubeParsingHelper {

    private YoutubeParsingHelper() {
    }

    public static long parseDurationString(String input)
            throws ParsingException, NumberFormatException {



        final String[] splitInput = input.contains(":")
                ? input.split(":")
                : input.split("\\.");

        String days = "0";
        String hours = "0";
        String minutes = "0";
        final String seconds;

        switch (splitInput.length) {
            case 4:
                days = splitInput[0];
                hours = splitInput[1];
                minutes = splitInput[2];
                seconds = splitInput[3];
                break;
            case 3:
                hours = splitInput[0];
                minutes = splitInput[1];
                seconds = splitInput[2];
                break;
            case 2:
                minutes = splitInput[0];
                seconds = splitInput[1];
                break;
            case 1:
                seconds = splitInput[0];
                break;
            default:
                throw new ParsingException("Error duration string with unknown format: " + input);
        }
        return ((((Long.parseLong(days) * 24)
                + Long.parseLong(hours) * 60)
                + Long.parseLong(minutes)) * 60)
                + Long.parseLong(seconds);
    }
}
