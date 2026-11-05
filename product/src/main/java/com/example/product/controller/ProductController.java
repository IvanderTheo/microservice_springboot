package com.example.product.controller;

import com.example.product.dto.CreateProductRequest;
import com.example.product.dto.UpdateProductRequest;
import com.example.product.dto.UpdateProductStockRequest;
import com.example.product.model.Product;
import com.example.product.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/products"})
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> index() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product show(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PatchMapping("/{id}")
    public Product patch(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        return productService.patch(id, request);
    }

    @PatchMapping("/{id}/stock")
    public Product reduceStock(@PathVariable Long id, @Valid @RequestBody UpdateProductStockRequest request) {
        return productService.adjustStock(id, request.quantity());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product store(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    public Product update(
            @PathVariable Long id,
            @Valid @RequestBody Product product
    ) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void destroy(@PathVariable Long id) {
        productService.delete(id);
    }
}
