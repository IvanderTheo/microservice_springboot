package com.example.order.dto;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class UpdateOrderRequestDeserializer extends StdDeserializer<UpdateOrderRequest> {

    public UpdateOrderRequestDeserializer() {
        super(UpdateOrderRequest.class);
    }

    @Override
    public UpdateOrderRequest deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode node = mapper.readTree(parser);

        List<UpdateOrderItemRequest> items;

        if (node.isArray()) {
            items = mapper.convertValue(
                    node,
                    mapper.getTypeFactory().constructCollectionType(List.class, UpdateOrderItemRequest.class));
        } else if (node.isObject() && node.has("productId")) {
            UpdateOrderItemRequest item = mapper.treeToValue(node, UpdateOrderItemRequest.class);
            items = List.of(item);
        } else if (node.isObject() && node.has("items")) {
            JsonNode itemsNode = node.get("items");
            if (!itemsNode.isArray()) {
                throw context.weirdStringException(itemsNode.toString(), List.class, "Items must be an array");
            }
            items = mapper.convertValue(
                    itemsNode,
                    mapper.getTypeFactory().constructCollectionType(List.class, UpdateOrderItemRequest.class));
        } else {
            throw JsonMappingException.from(
                    parser,
                    "Expected an array, a single order item, or an object containing items");
        }

        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setItems(items);
        return request;
    }
}
