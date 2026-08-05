package com.boutique.order.service;

import com.boutique.order.dto.CreateOrderRequest;
import com.boutique.order.dto.OrderResponse;
import com.boutique.order.entity.CustomerOrder;
import com.boutique.order.entity.OrderItem;
import com.boutique.order.event.OrderConfirmedEvent;
import com.boutique.order.exception.OrderNotFoundException;
import com.boutique.order.outbox.OutboxEvent;
import com.boutique.order.outbox.OutboxEventRepository;
import com.boutique.order.repository.OrderRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final String orderEventsTopic;

    public OrderService(
            OrderRepository repository,
            OutboxEventRepository outboxRepository,
            ObjectMapper objectMapper,
            @Value(
                    "${kafka.topics.order-events:"
                    + "boutique.order.events}"
            )
            String orderEventsTopic
    ) {
        this.repository = repository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.orderEventsTopic = orderEventsTopic;
    }

    @Transactional
    public OrderResponse create(
            CreateOrderRequest request
    ) {
        return repository
                .findByIdempotencyKey(
                        request.idempotencyKey()
                )
                .map(this::map)
                .orElseGet(() -> {
                    CustomerOrder order =
                            new CustomerOrder(
                                    UUID.randomUUID(),
                                    request.userId(),
                                    request.idempotencyKey(),
                                    request.total(),
                                    request.currency()
                            );

                    request.items().forEach(
                            item ->
                                    order.addItem(
                                            new OrderItem(
                                                    item.productId(),
                                                    item.sku(),
                                                    item.name(),
                                                    item.unitPrice(),
                                                    item.quantity(),
                                                    item.lineTotal()
                                            )
                                    )
                    );

                    return map(repository.save(order));
                });
    }

    @Transactional(readOnly = true)
    public OrderResponse get(UUID id) {
        return map(find(id));
    }

    @Transactional
    public OrderResponse confirm(
            UUID id,
            UUID paymentId
    ) {
        CustomerOrder order = find(id);

        // Do not create a second event for an idempotent repeat.
        if (paymentId.equals(order.getPaymentId())
                && order.getStatus().name().equals("CONFIRMED")) {
            return map(order);
        }

        order.confirmPayment(paymentId);

        OrderConfirmedEvent event =
                OrderConfirmedEvent.create(
                        order.getId(),
                        order.getUserId(),
                        paymentId,
                        order.getTotal(),
                        order.getCurrency()
                );

        outboxRepository.save(
                new OutboxEvent(
                        event.eventId(),
                        order.getId(),
                        orderEventsTopic,
                        order.getId().toString(),
                        event.eventType(),
                        serialize(event)
                )
        );

        return map(order);
    }

    @Transactional
    public OrderResponse fail(UUID id) {
        CustomerOrder order = find(id);
        order.failPayment();
        return map(order);
    }

    private CustomerOrder find(UUID id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new OrderNotFoundException(id)
                );
    }

    private String serialize(
            OrderConfirmedEvent event
    ) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Could not serialize order event.",
                    exception
            );
        }
    }

    private OrderResponse map(CustomerOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getIdempotencyKey(),
                order.getStatus(),
                order.getItems()
                        .stream()
                        .map(
                                item ->
                                        new OrderResponse.Item(
                                                item.getProductId(),
                                                item.getSku(),
                                                item.getName(),
                                                item.getUnitPrice(),
                                                item.getQuantity(),
                                                item.getLineTotal()
                                        )
                        )
                        .toList(),
                order.getTotal(),
                order.getCurrency(),
                order.getPaymentId(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getVersion()
        );
    }
}
