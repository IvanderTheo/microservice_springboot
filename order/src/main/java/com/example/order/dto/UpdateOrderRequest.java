package com.example.order.dto;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@JsonDeserialize(using = UpdateOrderRequestDeserializer.class)
public class UpdateOrderRequest {
    @NotEmpty(message = "At least one order item is required")
    private List<@Valid UpdateOrderItemRequest> items;

    public List<UpdateOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<UpdateOrderItemRequest> items) {
        this.items = items;
    }
}
