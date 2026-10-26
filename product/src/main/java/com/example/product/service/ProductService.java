
package com.example.product.service;

import com.example.product.dto.CreateProductRequest;
import com.example.product.dto.PatchProductRequest;
import com.example.product.dto.UpdateProductRequest;
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

    // GET: menampilkan seluruh produk
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // GET: menampilkan produk berdasarkan ID
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found")
                );
    }

    // POST: membuat produk baru
    public Product create(CreateProductRequest request) {
        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setStock(request.stock());

        return productRepository.save(product);
    }

    // PUT: memperbarui seluruh data produk
    public Product update(Long id, UpdateProductRequest request) {
        Product product = findById(id);

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setStock(request.stock());

        return productRepository.save(product);
    }

    // PATCH: memperbarui hanya field yang dikirim
    public Product patch(Long id, PatchProductRequest request) {
        Product product = findById(id);

        if (request.name() != null) {
            product.setName(request.name());
        }

        if (request.description() != null) {
            product.setDescription(request.description());
        }

        if (request.category() != null) {
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

    // DELETE: menghapus produk berdasarkan ID
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}
