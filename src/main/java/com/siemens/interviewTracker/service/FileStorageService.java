package com.siemens.interviewTracker.service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
@Service
public class FileStorageService {

    public static final String STORAGE_DIRECTORY = "storage/cv"; // Directory for storing CVs

    public String saveFile(MultipartFile fileToSave) throws IOException {
        if (fileToSave == null) {
            throw new NullPointerException("fileToSave is null");
        }

        // Validate file type
        if (!"application/pdf".equals(fileToSave.getContentType())) {
            throw new IllegalArgumentException("Invalid file format. Only PDF files are allowed.");
        }

        // Ensure the storage directory exists; if not, create it
        Path storageDirPath = Path.of(STORAGE_DIRECTORY);
        if (!Files.exists(storageDirPath)) {
            Files.createDirectories(storageDirPath);
        }

        // Generate a unique filename using UUID and the original file extension
        String originalFilename = fileToSave.getOriginalFilename();
        String fileExtension = getFileExtension(Objects.requireNonNull(originalFilename));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Define the target file path
        File targetFile = new File(storageDirPath.toFile(), uniqueFilename);

        // Save the file
        Files.copy(fileToSave.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Return the filename (without the storage directory or BASE_URL)
        return uniqueFilename;
    }

    private String getFileExtension(String filename) {
        int lastIndexOfDot = filename.lastIndexOf('.');
        return (lastIndexOfDot != -1) ? filename.substring(lastIndexOfDot) : ""; // Returns file extension with dot
    }

    public Resource getFile(String filename) throws IOException {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        // Construct the file path
        Path filePath = Path.of(STORAGE_DIRECTORY, filename);

        // Ensure the file exists
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filePath);
        }

        // Load the file as a Resource
        Resource fileResource = new UrlResource(filePath.toUri());
        if (!fileResource.exists() || !fileResource.isReadable()) {
            throw new IOException("File cannot be read: " + filePath);
        }

        return fileResource;
    }
}
