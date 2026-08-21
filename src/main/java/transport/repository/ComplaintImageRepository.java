package transport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import transport.entity.ComplaintImage;

public interface ComplaintImageRepository extends JpaRepository<ComplaintImage, Long> {
    List<ComplaintImage> findByComplaint_Id(Long complaintId);
}