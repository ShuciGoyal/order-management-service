package com.example.oms.web;

import com.example.oms.dto.request.PaymentRequest;
import com.example.oms.dto.response.OrderResponse;
import com.example.oms.mapper.OrderMapper;
import com.example.oms.service.PaymentService;
import com.example.oms.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> pay(@PathVariable UUID id, @RequestBody @Valid PaymentRequest req) {
    paymentService.recordPayment(id, req.amountCents(), req.method());
    var updated = orderService.findById(id);
        return ResponseEntity.ok(OrderMapper.toResponse(updated));
    }
}
