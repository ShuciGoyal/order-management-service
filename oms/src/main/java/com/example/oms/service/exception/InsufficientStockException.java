package com.example.oms.service.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) { super(message); }
}
