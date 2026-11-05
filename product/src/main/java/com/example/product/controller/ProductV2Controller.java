package com.example.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.product.dto.CreateProductRequest;
import com.example.product.dto.ProductV2Response;
import com.example.product.dto.UpdateProductRequest;
import com.example.product.service.ProductV2Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/products")
public class ProductV2Controller {
    private final ProductV2Service productService;

    public ProductV2Controller(ProductV2Service productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductV2Response> index() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ProductV2Response show(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductV2Response store(
            @Valid @ModelAttribute CreateProductRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return productService.create(request, image);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductV2Response update(
            @PathVariable Long id,
            @Valid @ModelAttribute CreateProductRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return productService.update(id, request, image);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductV2Response patch(
            @PathVariable Long id,
            @Valid @ModelAttribute UpdateProductRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return productService.patch(id, request, image);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void destroy(@PathVariable Long id) {
        productService.delete(id);
    }
}
