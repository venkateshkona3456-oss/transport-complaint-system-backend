package transport.controller;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import transport.entity.Bus;
import transport.entity.Route;
import transport.entity.User;
import transport.repository.BusRepository;
import transport.repository.RouteRepository;
import transport.repository.StudentRepository;
import transport.repository.UserRepository;
import transport.service.ComplaintService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final ComplaintService complaintService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public AdminController(
            ComplaintService complaintService,
            UserRepository userRepository,
            StudentRepository studentRepository,
            BusRepository busRepository,
            RouteRepository routeRepository) {

        this.complaintService = complaintService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    // =========================
    // ADMIN CHECK
    // =========================
   private boolean isAdmin() {

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        return false;
    }

    String email = authentication.getName();

    if (email == null || email.equals("anonymousUser")) {
        return false;
    }

    User user = userRepository.findByEmail(email).orElse(null);

    return user != null
            && user.getRole() != null
            && "ROLE_ADMIN".equalsIgnoreCase(user.getRole().toString());
}
    // =========================
    // GET ALL COMPLAINTS
    // =========================
    @GetMapping("/complaints")
    public Object getAllComplaints() {

        if (!isAdmin()) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access only");
        }

        return complaintService.findAll();
    }

    // =========================
    // GET ALL USERS
    // =========================
    @GetMapping("/users")
    public Object getAllUsers() {

        if (!isAdmin()) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access only");
        }

        List<User> users = userRepository.findAll();

        for (User user : users) {
            user.setPassword(null);
        }

        return users;
    }

    // =========================
    // GET ALL BUSES
    // =========================
    @GetMapping("/buses")
    public Object getAllBuses() {

        if (!isAdmin()) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access only");
        }

        return busRepository.findAll();
    }

    // =========================
    // CREATE BUS
    // =========================
    @PostMapping("/buses")
    public Object createBus(@RequestBody Bus bus) {

        if (!isAdmin()) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access only");
        }

        return busRepository.save(bus);
    }

    // =========================
    // GET ALL ROUTES
    // =========================
    @GetMapping("/routes")
    public Object getAllRoutes() {

        if (!isAdmin()) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access only");
        }

        return routeRepository.findAll();
    }

    // =========================
    // CREATE ROUTE
    // =========================
    @PostMapping("/routes")
    public Object createRoute(@RequestBody Route route) {

        if (!isAdmin()) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access only");
        }

        return routeRepository.save(route);
    }

    // =========================
    // REPORTS
    // =========================
    @GetMapping("/reports")
    public Object getReports() {

        if (!isAdmin()) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access only");
        }

        List<transport.entity.Complaint> allComplaints =
                complaintService.findAll();

        Map<String, Long> byStatus =
                new LinkedHashMap<>();

        Map<String, Long> byCategory =
                new LinkedHashMap<>();

        Map<String, Long> byPriority =
                new LinkedHashMap<>();

        for (transport.entity.Complaint complaint : allComplaints) {

            byStatus.merge(
                    complaint.getStatus(),
                    1L,
                    Long::sum
            );

            byCategory.merge(
                    complaint.getCategory(),
                    1L,
                    Long::sum
            );

            byPriority.merge(
                    complaint.getPriority(),
                    1L,
                    Long::sum
            );
        }

        Map<String, Object> report =
                new LinkedHashMap<>();

        report.put(
                "totalComplaints",
                allComplaints.size()
        );

        report.put(
                "totalUsers",
                userRepository.findAll().size()
        );

        report.put(
                "totalBuses",
                busRepository.findAll().size()
        );

        report.put(
                "totalRoutes",
                routeRepository.findAll().size()
        );

        report.put(
                "byStatus",
                byStatus
        );

        report.put(
                "byCategory",
                byCategory
        );

        report.put(
                "byPriority",
                byPriority
        );

        return report;
    }
}