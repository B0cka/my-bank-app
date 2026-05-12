package com.b0cka.ex;

public class InvalidAction extends RuntimeException {
    public InvalidAction(String message) {
        super(message);
    }
}
