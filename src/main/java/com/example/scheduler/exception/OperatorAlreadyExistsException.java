package com.example.scheduler.exception;

public class OperatorAlreadyExistsException extends RuntimeException {
    OperatorAlreadyExistsException(String msg) {
        super(msg);
    }
}
