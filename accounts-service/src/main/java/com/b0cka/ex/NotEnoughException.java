package com.b0cka.ex;

public class NotEnoughException extends RuntimeException {
    public NotEnoughException(String message) {
        super(message);
    }
}
