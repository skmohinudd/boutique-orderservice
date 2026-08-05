package com.boutique.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID userId,
        @NotBlank @Size(max = 100) String idempotencyKey,
        @NotEmpty List<@Valid Item> items,
        @NotNull @DecimalMin("0.01") BigDecimal total,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency
) {
    public record Item(
            @NotNull UUID productId,
            @NotBlank String sku,
            @NotBlank String name,
            @NotNull @DecimalMin("0.00") BigDecimal unitPrice,
            @Min(1) @Max(99) int quantity,
            @NotNull @DecimalMin("0.00") BigDecimal lineTotal
    ) {}
}
