package com.example.order.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class CreateOrderRequest {
    @NotEmpty(message = "At least one order item is required")
    private List<@Valid CreateOrderItemRequest> items;

    public List<CreateOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemRequest> items) {
        this.items = items;
    }
}
