package com.example.product.service;

import com.example.product.dto.CreateProductRequest;
import com.example.product.dto.UpdateProductRequest;
import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
                );
    }

    public Product create(CreateProductRequest request) {

        Product product = new Product();

        product.setName(request.name());
        product.setCategory(request.category());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());

        return productRepository.save(product);
    }

    public Product update(Long id, Product data) {
        Product product = findById(id);

        product.setName(data.getName());
        product.setDescription(data.getDescription());
        product.setCategory(data.getCategory());
        product.setPrice(data.getPrice());
        product.setStock(data.getStock());

        return productRepository.save(product);
    }

    public Product patch(Long id, UpdateProductRequest request) {
        Product product = findById(id);

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

        return productRepository.save(product);
    }

    public Product adjustStock(Long id, int quantity) {
        Product product = findById(id);

        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        }
        if (product.getStock() < quantity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock for product " + product.getId());
        }

        product.setStock(product.getStock() - quantity);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}