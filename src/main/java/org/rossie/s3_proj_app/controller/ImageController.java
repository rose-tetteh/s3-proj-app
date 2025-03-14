package org.rossie.s3_proj_app.controller;

import org.rossie.s3_proj_app.model.ImageDto;
import org.rossie.s3_proj_app.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")

@CrossOrigin(origins = "*")
public class ImageController {

    @Autowired
    private ImageService s3Service;

    public ImageController (ImageService s3Service){
        this.s3Service = s3Service;
    }

    @GetMapping
    public ResponseEntity<List<ImageDto>> listImages() {
        return ResponseEntity.ok(s3Service.listImages());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title
    ) throws IOException {
        s3Service.uploadImage(file, title);
        return ResponseEntity.ok("Image uploaded successfully");
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> deleteImage(@PathVariable String key) {
        s3Service.deleteImage(key);
        return ResponseEntity.ok("Image deleted successfully");
    }
}
