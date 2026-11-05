package com.example.product.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Component
public class LocalFileProductImageStorage implements ProductImageStorage {
    private static final String IMAGE_PATH_PREFIX = "/product-images/";
    private static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif");

    private final Path storageDirectory;

    public LocalFileProductImageStorage(
            @Value("${product.image.storage-dir:uploads/products}") String storageDirectory) {
        this.storageDirectory = Path.of(storageDirectory).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image must not exceed 5 MB");
        }

        String contentType = image.getContentType();
        String extension = contentType == null
                ? null
                : EXTENSIONS_BY_CONTENT_TYPE.get(contentType.toLowerCase(Locale.ROOT));
        if (extension == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Image must be JPEG, PNG, WebP, or GIF");
        }

        String filename = UUID.randomUUID() + extension;
        Path destination = storageDirectory.resolve(filename).normalize();
        if (!destination.startsWith(storageDirectory)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image path");
        }

        try {
            Files.createDirectories(storageDirectory);
            try (InputStream input = image.getInputStream()) {
                Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store product image", exception);
        }

        return IMAGE_PATH_PREFIX + filename;
    }

    @Override
    public void delete(String imagePath) {
        if (imagePath == null) {
            return;
        }
        if (!imagePath.startsWith(IMAGE_PATH_PREFIX)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid stored product image path");
        }

        String filename = imagePath.substring(IMAGE_PATH_PREFIX.length());
        if (filename.isBlank() || filename.contains("/") || filename.contains("\\")) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid stored product image path");
        }

        Path image = storageDirectory.resolve(filename).normalize();
        if (!image.startsWith(storageDirectory)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid stored product image path");
        }
        try {
            Files.deleteIfExists(image);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not delete product image", exception);
        }
    }
}
