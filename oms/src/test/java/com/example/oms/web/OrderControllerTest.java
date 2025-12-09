package com.example.oms.web;

import com.example.oms.dto.request.CreateOrderRequest;
import com.example.oms.dto.request.OrderItemRequest;
import com.example.oms.model.Order;
import com.example.oms.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderControllerTest {

    @Test
    void createAndGet() {
        var svc = Mockito.mock(OrderService.class);
        var ctrl = new OrderController(svc);

    UUID custId = UUID.randomUUID();
    var req = new CreateOrderRequest(custId, List.of(new OrderItemRequest(UUID.randomUUID(), 2)));
    var saved = new Order(); UUID id = UUID.randomUUID(); saved.setId(id);
    // populate customer and items so mapper can create response
    var cust = new com.example.oms.model.Customer(); cust.setId(req.customerId()); saved.setCustomer(cust);
    var item = new com.example.oms.model.OrderItem(); item.setId(UUID.randomUUID());
    var prod = new com.example.oms.model.Product(); prod.setId(req.items().get(0).productId()); item.setProduct(prod);
    item.setQuantity(req.items().get(0).quantity()); item.setUnitPriceCents(999L);
    saved.setItems(java.util.List.of(item));
        Mockito.when(svc.createOrder(Mockito.any(Order.class))).thenReturn(saved);

        var resp = ctrl.create(req);
        assertThat(resp.getStatusCode().value()).isEqualTo(201);

        Mockito.when(svc.findById(id)).thenReturn(saved);
        var get = ctrl.get(id);
        assertThat(get.getStatusCode().value()).isEqualTo(200);
    // update
    var updated = new Order(); updated.setId(id);
    // ensure customer present on updated order so mapper can build response
    updated.setCustomer(cust);
    Mockito.when(svc.update(Mockito.eq(id), Mockito.any(Order.class))).thenReturn(updated);
    var up = ctrl.update(id, req);
    assertThat(up.getStatusCode().value()).isEqualTo(200);

    // delete
    Mockito.doNothing().when(svc).delete(id);
    var del = ctrl.delete(id);
    assertThat(del.getStatusCode().value()).isEqualTo(204);
    }
}
