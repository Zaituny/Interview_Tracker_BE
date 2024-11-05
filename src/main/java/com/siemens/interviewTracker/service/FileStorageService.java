package com.siemens.interviewTracker.service;
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
    public static final String BASE_URL = "/cv/"; // Base URL to access CVs

    public String saveFile(MultipartFile fileToSave) throws IOException {
        if (fileToSave == null) {
            throw new NullPointerException("fileToSave is null");
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

        // Return the relative path for accessing the file in the application
        return BASE_URL + uniqueFilename;
    }

    private String getFileExtension(String filename) {
        int lastIndexOfDot = filename.lastIndexOf('.');
        return (lastIndexOfDot != -1) ? filename.substring(lastIndexOfDot) : ""; // Returns file extension with dot
    }
}
