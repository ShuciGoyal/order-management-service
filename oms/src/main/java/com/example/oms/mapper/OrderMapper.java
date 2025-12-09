package com.example.oms.mapper;

import com.example.oms.dto.request.CreateOrderRequest;
 
import com.example.oms.dto.response.OrderItemResponse;
import com.example.oms.dto.response.OrderResponse;
import com.example.oms.model.Order;
import com.example.oms.model.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public final class OrderMapper {
    private OrderMapper() {}

    public static OrderResponse toResponse(Order o) {
        if (o == null) return null;
    List<OrderItemResponse> items = o.getItems().stream().map(it ->
        new OrderItemResponse(it.getId(), it.getProduct().getId(), it.getQuantity(), it.getUnitPriceCents())
    ).collect(Collectors.toList());

    return new OrderResponse(o.getId(), o.getCustomer().getId(), items, o.getStatus(), o.getCreatedAt());
    }

    public static Order toEntity(CreateOrderRequest req) {
        if (req == null) return null;
        var order = new Order();
        // Note: we only set the relation later in service layer where Customer/Product are loaded.
        List<OrderItem> items = req.items().stream().map(it -> {
            var oi = new OrderItem();
            // product and order associations will be set by the service during creation
            oi.setQuantity(it.quantity());
            return oi;
        }).collect(Collectors.toList());
        order.setItems(items);
        return order;
    }
}
