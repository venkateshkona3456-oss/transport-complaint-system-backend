package transport.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import transport.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}