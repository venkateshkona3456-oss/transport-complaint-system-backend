package transport.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

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

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    public ComplaintImageController(ComplaintImageRepository complaintImageRepository, ComplaintService complaintService) {
        this.complaintImageRepository = complaintImageRepository;
        this.complaintService = complaintService;
    }

    private Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Complaint complaint = complaintService.findById(id);
            if (complaint == null) {
                return ResponseEntity.status(404).body("Complaint not found");
            }

            Cloudinary cloudinary = getCloudinary();
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");

            ComplaintImage image = new ComplaintImage();
            image.setComplaint(complaint);
            image.setImageUrl(imageUrl);
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
}