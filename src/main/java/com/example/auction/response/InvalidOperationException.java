package com.example.auction.response;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException() {
    }

    public InvalidOperationException(String message) {
        super(message);
    }
}
