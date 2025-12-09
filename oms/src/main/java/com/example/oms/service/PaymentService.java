package com.example.oms.service;

import com.example.oms.model.Payment;
import java.util.UUID;

public interface PaymentService {
    Payment recordPayment(UUID orderId, long amountCents, String method);
}
