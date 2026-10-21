package com.example.product.service;

import com.example.product.dto.CreateProductRequest;
import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

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
                        new RuntimeException("Product not found")
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

    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}