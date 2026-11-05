package com.example.order.dto;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class CreateOrderRequestDeserializer
        extends StdDeserializer<CreateOrderRequest> {

    public CreateOrderRequestDeserializer() {
        super(CreateOrderRequest.class);
    }

    @Override
    public CreateOrderRequest deserialize(
            JsonParser parser,
            DeserializationContext context) throws IOException {

        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode node = mapper.readTree(parser);

        List<CreateOrderItemRequest> items;

        if (node.isArray()) {
            items = mapper.convertValue(
                    node,
                    mapper.getTypeFactory()
                            .constructCollectionType(
                                    List.class,
                                    CreateOrderItemRequest.class));
        } else if (node.isObject() && node.has("productId")) {
            CreateOrderItemRequest item =
                    mapper.treeToValue(node, CreateOrderItemRequest.class);

            items = List.of(item);
        } else if (node.isObject() && node.has("items")) {
            JsonNode itemsNode = node.get("items");

            if (itemsNode.isArray()) {
                items = mapper.convertValue(
                        itemsNode,
                        mapper.getTypeFactory()
                                .constructCollectionType(
                                        List.class,
                                        CreateOrderItemRequest.class));
            } else {
                throw context.weirdStringException(
                        itemsNode.toString(),
                        List.class,
                        "Items must be an array");
            }
        } else {
            throw JsonMappingException.from(
        parser,
        "Expected an array, a single order item, or an object containing items"
);
        }

        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(items);

        return request;
    }
}
