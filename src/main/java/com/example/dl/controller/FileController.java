package com.example.dl.controller;

import com.example.dl.entity.FileEntity;
import com.example.dl.service.MinIOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    
    private final MinIOService minIOService;
    
    private static final String[] ALLOWED_EXTENSIONS = {".png", ".txt", ".json"};
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("File is empty"));
            }
            
            // Валидация типа файла
            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("File name is empty"));
            }
            
            String extension = getFileExtension(originalName).toLowerCase();
            if (!isAllowedExtension(extension)) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Неправильный тип файла. Разрешены только: png, txt, json"));
            }
            
            FileEntity fileEntity = minIOService.uploadFile(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "File uploaded successfully");
            response.put("fileId", fileEntity.getId());
            response.put("originalName", fileEntity.getOriginalName());
            response.put("storedName", fileEntity.getStoredName());
            response.put("fileSize", fileEntity.getFileSize());
            response.put("contentType", fileEntity.getContentType());
            response.put("uploadedAt", fileEntity.getCreatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error uploading file: " + e.getMessage()));
        }
    }
    
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}

