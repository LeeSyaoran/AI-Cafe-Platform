package com.example.backend.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final String code;
    private final String message;
    private final int status;

    public ApiException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public ApiException(int status, String code, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
