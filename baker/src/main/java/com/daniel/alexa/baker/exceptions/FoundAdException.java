package com.daniel.alexa.baker.exceptions;

public class FoundAdException extends ParsingException {
    public FoundAdException(String message) {
        super(message);
    }

    public FoundAdException(String message, Throwable cause) {
        super(message, cause);
    }
}
