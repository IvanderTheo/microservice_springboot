package com.example.product.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ProductImageStorage {
    String store(MultipartFile image);

    void delete(String imagePath);
}
