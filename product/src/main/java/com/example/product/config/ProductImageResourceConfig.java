package com.example.product.config;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ProductImageResourceConfig implements WebMvcConfigurer {
    private final Path storageDirectory;

    public ProductImageResourceConfig(
            @Value("${product.image.storage-dir:uploads/products}") String storageDirectory) {
        this.storageDirectory = Path.of(storageDirectory).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = storageDirectory.toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/product-images/**")
                .addResourceLocations(location);
    }
}
