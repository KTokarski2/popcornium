package com.teg.popcornium_api.integrations.minio;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@PermitAll
public class FileController {

    private final MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam ("file") MultipartFile file) {
        String generatedName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        minioService.upload(generatedName, file);
        return ResponseEntity.ok(generatedName);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName) {
        byte[] data = minioService.download(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(data);
    }
}
