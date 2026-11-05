package com.example.product.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.product.dto.CreateProductRequest;
import com.example.product.dto.ProductV2Response;
import com.example.product.dto.UpdateProductRequest;
import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;
import com.example.product.storage.ProductImageStorage;

@Service
public class ProductV2Service {
    private final ProductRepository productRepository;
    private final ProductImageStorage imageStorage;

    public ProductV2Service(ProductRepository productRepository, ProductImageStorage imageStorage) {
        this.productRepository = productRepository;
        this.imageStorage = imageStorage;
    }

    public List<ProductV2Response> findAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductV2Response findById(Long id) {
        return toResponse(findEntityById(id));
    }

    public ProductV2Response create(CreateProductRequest request, MultipartFile image) {
        Product product = new Product();
        applyRequiredFields(product, request);
        return saveWithOptionalImage(product, image, null);
    }

    public ProductV2Response update(Long id, CreateProductRequest request, MultipartFile image) {
        Product product = findEntityById(id);
        applyRequiredFields(product, request);
        return saveWithOptionalImage(product, image, product.getImagePath());
    }

    public ProductV2Response patch(Long id, UpdateProductRequest request, MultipartFile image) {
        Product product = findEntityById(id);

        if (request.name() != null && !request.name().isBlank()) {
            product.setName(request.name());
        }
        if (request.description() != null && !request.description().isBlank()) {
            product.setDescription(request.description());
        }
        if (request.category() != null && !request.category().isBlank()) {
            product.setCategory(request.category());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.stock() != null) {
            product.setStock(request.stock());
        }

        return saveWithOptionalImage(product, image, product.getImagePath());
    }

    public void delete(Long id) {
        Product product = findEntityById(id);
        String imagePath = product.getImagePath();
        productRepository.delete(product);
        imageStorage.delete(imagePath);
    }

    private ProductV2Response saveWithOptionalImage(Product product, MultipartFile image, String previousImagePath) {
        String newImagePath = imageStorage.store(image);
        if (newImagePath != null) {
            product.setImagePath(newImagePath);
        }

        Product saved;
        try {
            saved = productRepository.save(product);
        } catch (RuntimeException exception) {
            if (newImagePath != null) {
                try {
                    imageStorage.delete(newImagePath);
                } catch (RuntimeException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            throw exception;
        }

        if (newImagePath != null && previousImagePath != null) {
            imageStorage.delete(previousImagePath);
        }
        return toResponse(saved);
    }

    private void applyRequiredFields(Product product, CreateProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setStock(request.stock());
    }

    private Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private ProductV2Response toResponse(Product product) {
        return new ProductV2Response(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getStock(),
                product.getImagePath(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
}
