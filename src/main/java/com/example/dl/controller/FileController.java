package com.example.dl.controller;

import com.example.dl.entity.FileEntity;
import com.example.dl.service.MinIOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    
    private final MinIOService minIOService;
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("File is empty"));
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
    
    @GetMapping("/download/{storedName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String storedName) {
        try {
            FileEntity fileEntity = minIOService.getFileInfo(storedName);
            InputStream inputStream = minIOService.downloadFile(storedName);
            
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + fileEntity.getOriginalName() + "\"")
                .contentType(MediaType.parseMediaType(fileEntity.getContentType()))
                .contentLength(fileEntity.getFileSize())
                .body(resource);
                
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/info/{storedName}")
    public ResponseEntity<Map<String, Object>> getFileInfo(@PathVariable String storedName) {
        try {
            FileEntity fileEntity = minIOService.getFileInfo(storedName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", fileEntity.getId());
            response.put("originalName", fileEntity.getOriginalName());
            response.put("storedName", fileEntity.getStoredName());
            response.put("fileSize", fileEntity.getFileSize());
            response.put("contentType", fileEntity.getContentType());
            response.put("extension", fileEntity.getExtension());
            response.put("createdAt", fileEntity.getCreatedAt());
            response.put("updatedAt", fileEntity.getUpdatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting file info: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{storedName}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable String storedName) {
        try {
            minIOService.deleteFile(storedName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "File deleted successfully");
            response.put("storedName", storedName);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error deleting file: " + e.getMessage()));
        }
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}
