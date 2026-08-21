package transport.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import transport.entity.Complaint;
import transport.entity.ComplaintImage;
import transport.repository.ComplaintImageRepository;
import transport.service.ComplaintService;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintImageController {

    private final ComplaintImageRepository complaintImageRepository;
    private final ComplaintService complaintService;
    private final String uploadDir = "uploads/complaint-images";

    public ComplaintImageController(ComplaintImageRepository complaintImageRepository, ComplaintService complaintService) {
        this.complaintImageRepository = complaintImageRepository;
        this.complaintService = complaintService;
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Complaint complaint = complaintService.findById(id);
            if (complaint == null) {
                return ResponseEntity.status(404).body("Complaint not found");
            }

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename();
            String extension = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID().toString() + extension;
            Path targetPath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            ComplaintImage image = new ComplaintImage();
            image.setComplaint(complaint);
            image.setImageUrl("/api/complaints/images/" + fileName);
            ComplaintImage saved = complaintImageRepository.save(image);

            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/images")
    public List<ComplaintImage> getImagesForComplaint(@PathVariable Long id) {
        return complaintImageRepository.findByComplaint_Id(id);
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}