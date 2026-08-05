package com.abdelrahman027.sbecom2.exception;

public class ApiException extends RuntimeException {
    private static final long serialVersion= 1L;

    public ApiException(){

    }

    public ApiException(String message) {
        super(message);
    }
}
