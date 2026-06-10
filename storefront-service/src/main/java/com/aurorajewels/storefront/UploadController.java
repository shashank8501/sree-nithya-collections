package com.aurorajewels.storefront;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {
  private final Path uploadPath;

  public UploadController(@Value("${upload.dir}") String uploadDir) throws IOException {
    this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    Files.createDirectories(this.uploadPath);
  }

  @PostMapping("/shop-api/admin/uploads")
  public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
    if (file.isEmpty() || file.getOriginalFilename() == null) {
      return ResponseEntity.badRequest().build();
    }
    String original = Paths.get(file.getOriginalFilename()).getFileName().toString();
    String extension = "";
    int dot = original.lastIndexOf('.');
    if (dot >= 0) {
      extension = original.substring(dot).toLowerCase();
    }
    String filename = UUID.randomUUID() + extension;
    Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
    return ResponseEntity.ok(Collections.singletonMap("path", "/uploads/" + filename));
  }
}
