package transport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import transport.entity.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudent_Id(Long studentId);
}