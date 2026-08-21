package transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import transport.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByUser_Email(String email);
}