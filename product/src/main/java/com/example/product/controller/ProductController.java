
package com.example.product.controller;

import com.example.product.dto.CreateProductRequest;
import com.example.product.dto.UpdateProductRequest;
import com.example.product.dto.PatchProductRequest;
import com.example.product.model.Product;
import com.example.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@OpenAPIDefinition(
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "API Gateway"
        )
    }
)
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Menampilkan seluruh produk")
    public List<Product> index() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Menampilkan detail produk berdasarkan ID")
    public Product show(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Menambahkan produk baru")
    public Product store(
            @Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Memperbarui seluruh data produk")
    public Product update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(id, request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Memperbarui sebagian data produk")
    public Product patch(
            @PathVariable Long id,
            @Valid @RequestBody PatchProductRequest request) {
        return productService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Menghapus produk berdasarkan ID")
    public void destroy(@PathVariable Long id) {
        productService.delete(id);
    }
}
