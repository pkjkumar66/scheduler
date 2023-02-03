package com.example.scheduler.exception;

public class NotAvailableSlot extends RuntimeException {
    public NotAvailableSlot(String msg) {
        super(msg);
    }
}
