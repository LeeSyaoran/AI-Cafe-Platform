package com.example.backend.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {

    private final String errorCode;

    public ForbiddenException(String message) {
        super(message);
        this.errorCode = "FORBIDDEN";
    }

    public ForbiddenException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
