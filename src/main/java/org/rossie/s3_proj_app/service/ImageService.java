package org.rossie.s3_proj_app.service;

import lombok.RequiredArgsConstructor;
import org.rossie.s3_proj_app.model.ImageDto;
import org.rossie.s3_proj_app.utils.S3Config;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private S3Client s3Client = S3Config.s3Client();

    private String bucketName = "s3appl-images";

    public ImageService(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public List<ImageDto> listImages() {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        return s3Client.listObjectsV2(listRequest).contents().stream()
                .map(s3Object -> {
                    // Try to extract title from the key or use a default
                    String title = extractTitleFromKey(s3Object.key());
                    return ImageDto.builder()
                            .key(s3Object.key())
                            .url(generatePresignedUrl(s3Object.key()))
                            .title(title)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void uploadImage(MultipartFile file, String title) throws IOException {
        // Include title in the key or metadata
        String key = (title != null && !title.trim().isEmpty()
                ? title.replaceAll("[^a-zA-Z0-9_]", "-") + "-"
                : "")
                + System.currentTimeMillis() + "-" + file.getOriginalFilename();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public void deleteImage(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    private String generatePresignedUrl(String key) {
        return s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucketName).key(key))
                .toString();
    }

    private String extractTitleFromKey(String key) {
        // Extract title from key or return default
        String[] parts = key.split("-");
        return parts.length > 2 ? parts[0].replace("-", " ") : "Untitled Image";
    }
}
