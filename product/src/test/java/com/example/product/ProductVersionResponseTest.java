package com.example.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.product.dto.ProductV2Response;
import com.example.product.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

class ProductVersionResponseTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void v1ProductDoesNotExposeImagePath() throws Exception {
        Product product = new Product();
        product.setImagePath("/product-images/example.png");

        String response = objectMapper.writeValueAsString(product);

        assertFalse(response.contains("imagePath"));
        assertFalse(response.contains("/product-images/"));
    }

    @Test
    void v2ProductResponseExposesImagePath() throws Exception {
        ProductV2Response product = new ProductV2Response(
                1L,
                "Product",
                "Description",
                "Category",
                BigDecimal.TEN,
                3,
                "/product-images/example.png",
                null,
                null);

        String response = objectMapper.writeValueAsString(product);

        assertTrue(response.contains("\"imagePath\":\"/product-images/example.png\""));
    }
}
