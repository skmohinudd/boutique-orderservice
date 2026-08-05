package com.boutique.order.controller;

import com.boutique.order.dto.*;
import com.boutique.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }

    @PostMapping
    ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + response.id())).body(response);
    }

    @GetMapping("/{orderId}")
    OrderResponse get(@PathVariable UUID orderId) { return service.get(orderId); }

    @PostMapping("/{orderId}/confirm")
    OrderResponse confirm(@PathVariable UUID orderId, @RequestParam UUID paymentId) {
        return service.confirm(orderId, paymentId);
    }

    @PostMapping("/{orderId}/payment-failed")
    OrderResponse fail(@PathVariable UUID orderId) { return service.fail(orderId); }
}
