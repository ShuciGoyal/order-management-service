package com.example.oms.web;

import com.example.oms.dto.request.CreateOrderRequest;
import com.example.oms.dto.response.OrderResponse;
import com.example.oms.mapper.OrderMapper;
import com.example.oms.model.Order;
import com.example.oms.service.OrderService;
import com.example.oms.service.CustomerService;
import com.example.oms.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;

    @Autowired
    public OrderController(OrderService orderService, CustomerService customerService, ProductService productService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.productService = productService;
    }

    // Backwards-compatible constructor for unit tests that only provide OrderService
    public OrderController(OrderService orderService) {
        this(orderService, null, null);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest req) {
        Order o = OrderMapper.toEntity(req);
        // If controller was constructed by Spring with services available, resolve relations.
        // Some unit tests construct controller with only OrderService; in that case leave wiring to service/mappers.
        if (customerService != null && productService != null) {
            var cust = customerService.findById(req.customerId());
            o.setCustomer(cust);
            for (int i = 0; i < req.items().size(); i++) {
                var itemReq = req.items().get(i);
                var prod = productService.findById(itemReq.productId());
                var oi = o.getItems().get(i);
                oi.setProduct(prod);
                oi.setOrder(o);
            }
        }
        var saved = orderService.createOrder(o);
        var resp = OrderMapper.toResponse(saved);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + saved.getId())).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable UUID id) {
        var o = orderService.findById(id);
        return ResponseEntity.ok(OrderMapper.toResponse(o));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable UUID id, @RequestBody CreateOrderRequest req) {
        Order o = OrderMapper.toEntity(req);
        var updated = orderService.update(id, o);
        return ResponseEntity.ok(OrderMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/place")
    public ResponseEntity<OrderResponse> place(@PathVariable UUID id) {
        var updated = orderService.placeOrder(id);
        return ResponseEntity.ok(OrderMapper.toResponse(updated));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable UUID id) {
        var updated = orderService.cancelOrder(id);
        return ResponseEntity.ok(OrderMapper.toResponse(updated));
    }
}
