package org.rossie.s3_proj_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rossie.s3_proj_app.model.ImageDto;
import org.rossie.s3_proj_app.utils.S3Config;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    public ImageService() {
        this.s3Client = S3Config.s3Client();
        this.s3Presigner = S3Config.s3Presigner();
        this.bucketName = "s3appl-images";
    }

    public List<ImageDto> listImages() {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            return s3Client.listObjectsV2(listRequest).contents().stream()
                    .map(s3Object -> {
                        String title = extractTitleFromKey(s3Object.key());
                        return ImageDto.builder()
                                .key(s3Object.key())
                                .url(generatePresignedUrl(s3Object.key()))
                                .title(title)
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error listing images from S3: {}", e.getMessage(), e);
            // Return empty list instead of throwing exception
            return new ArrayList<>();
        }
    }

    public void uploadImage(MultipartFile file, String title) throws IOException {
        try {
            // Include title in the key or metadata
            String key = (title != null && !title.trim().isEmpty() ?
                    title.replaceAll("[^a-zA-Z0-9_]", "-") + "-" : "") +
                    System.currentTimeMillis() + "-" + file.getOriginalFilename();

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()));
        } catch (Exception e) {
            log.error("Error uploading image to S3: {}", e.getMessage(), e);
            throw new IOException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    public void deleteImage(String key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            log.error("Error deleting image from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete image: " + e.getMessage(), e);
        }
    }

    private String generatePresignedUrl(String key) {
        try {
            // Create a proper presigned URL with expiration time
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))  // URL expires in 1 hour
                    .getObjectRequest(getObjectRequest)
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage(), e);
            // Fallback to direct URL if presigning fails
            return s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucketName).key(key))
                    .toString();
        }
    }

    private String extractTitleFromKey(String key) {
        // Extract title from key or return default
        String[] parts = key.split("-");
        return parts.length > 2 ? parts[0].replace("-", " ") : "Untitled Image";
    }
}