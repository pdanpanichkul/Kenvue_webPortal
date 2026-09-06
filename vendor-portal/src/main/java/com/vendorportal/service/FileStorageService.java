package com.vendorportal.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadRoot, e);
        }
    }

    /**
     * Saves a file under a per-PO subfolder with a randomized name to avoid collisions,
     * and returns the generated stored filename (relative to that PO's folder).
     */
    public String store(MultipartFile file, String poReference) {
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) {
            extension = originalName.substring(dot);
        }
        String storedFileName = UUID.randomUUID() + extension;

        try {
            Path poFolder = uploadRoot.resolve(sanitize(poReference));
            Files.createDirectories(poFolder);

            Path target = poFolder.resolve(storedFileName).normalize();
            if (!target.startsWith(poFolder)) {
                throw new SecurityException("Invalid file path");
            }

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return storedFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + originalName, e);
        }
    }

    public Path resolve(String poReference, String storedFileName) {
        return uploadRoot.resolve(sanitize(poReference)).resolve(storedFileName).normalize();
    }

    private String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}
