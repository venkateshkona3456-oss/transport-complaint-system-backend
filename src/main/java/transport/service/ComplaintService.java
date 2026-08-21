package transport.service;

import java.util.List;

import org.springframework.stereotype.Service;

import transport.entity.Complaint;
import transport.repository.ComplaintRepository;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public List<Complaint> findAll() {
        return complaintRepository.findAll();
    }

    public List<Complaint> findByStudentId(Long studentId) {
        return complaintRepository.findByStudent_Id(studentId);
    }

    public Complaint findById(Long id) {
        return complaintRepository.findById(id).orElse(null);
    }

    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }
}