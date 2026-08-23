package transport.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import transport.entity.Complaint;
import transport.entity.Notification;
import transport.entity.Student;
import transport.entity.User;
import transport.repository.NotificationRepository;
import transport.repository.StudentRepository;
import transport.repository.UserRepository;
import transport.service.ComplaintService;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final StudentRepository studentRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public ComplaintController(ComplaintService complaintService, StudentRepository studentRepository,
            NotificationRepository notificationRepository, UserRepository userRepository) {
        this.complaintService = complaintService;
        this.studentRepository = studentRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Complaint> getAllComplaints() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByUser_Email(email);
        if (student != null) {
            return complaintService.findByStudentId(student.getId());
        }
        return complaintService.findAll();
    }

    @GetMapping("/{id}")
    public Complaint getComplaintById(@PathVariable Long id) {
        return complaintService.findById(id);
    }

    @PostMapping
    public Complaint createComplaint(@RequestBody Complaint complaint) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByUser_Email(email);
        complaint.setStudent(student);
        return complaintService.save(complaint);
    }

    @PutMapping("/{id}/status")
    public org.springframework.http.ResponseEntity<?> updateComplaintStatus(@PathVariable Long id, @RequestBody Complaint statusUpdate) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);

        boolean isAdmin = user != null && user.getRole() != null
                && "ROLE_ADMIN".equalsIgnoreCase(user.getRole().toString());

        if (!isAdmin) {
            return org.springframework.http.ResponseEntity.status(403).body("Only admin can update complaint status");
        }

        Complaint existing = complaintService.findById(id);
        existing.setStatus(statusUpdate.getStatus());
        Complaint updated = complaintService.save(existing);

        if (updated.getStudent() != null && updated.getStudent().getUser() != null) {
            Notification notification = new Notification();
            notification.setUser(updated.getStudent().getUser());
            notification.setComplaint(updated);
            notification.setTitle("Complaint Status Updated");
            notification.setMessage("Your complaint '" + updated.getTitle() + "' status changed to " + updated.getStatus());
            notificationRepository.save(notification);
        }

        return org.springframework.http.ResponseEntity.ok(updated);
    }
}