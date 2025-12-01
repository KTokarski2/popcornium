package com.teg.popcornium_api.integrations.minio;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String popcorniumBucketName;

    @PostConstruct
    public void initializeBucket() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(popcorniumBucketName).build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(popcorniumBucketName).build()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not initialize MinIO bucket", e);
        }
    }

    public String upload(String fileName, MultipartFile file) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(popcorniumBucketName)
                            .object(fileName)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    public byte[] download(String fileName) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(popcorniumBucketName)
                        .object(fileName)
                        .build()
        )) {
            return stream.readAllBytes();

        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    public InputStream getFile(String filename) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(popcorniumBucketName)
                        .object(filename)
                        .build()
        );
    }
}
