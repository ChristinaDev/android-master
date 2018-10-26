package com.daniel.alexa.baker.exceptions;

public class ContentNotAvailableException extends ParsingException {
    public ContentNotAvailableException(String message) {
        super(message);
    }

    public ContentNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
