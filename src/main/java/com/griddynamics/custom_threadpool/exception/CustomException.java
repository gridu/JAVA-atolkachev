package com.griddynamics.custom_threadpool.exception;

public class CustomException extends RuntimeException {

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
