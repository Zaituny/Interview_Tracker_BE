package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/cvs")
public class FileStorageController {
    private final FileStorageService fileStorageService;
    private static final Logger logger = LoggerFactory.getLogger(FileStorageController.class);

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Upload a CV file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File uploaded successfully",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "No file provided",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Error saving the CV",
                    content = @Content(mediaType = "text/plain"))
    })
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadCv(
            @RequestParam("file")
            @Valid MultipartFile file) {
        try {
            logger.info("Uploading file: " + file.getOriginalFilename());
            String filePath = fileStorageService.saveFile(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(filePath);
        } catch (IOException e) {
            logger.error("Error saving the CV file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving the CV: " + e.getMessage());
        } catch (NullPointerException e) {
            logger.error("No file provided", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file provided");
        }
    }
}
