package com.B0cka.ex;

public class InvalidAmount extends RuntimeException {
    public InvalidAmount(String message) {
        super(message);
    }
}
