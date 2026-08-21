package transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import transport.entity.Bus;

public interface BusRepository extends JpaRepository<Bus, Long> {
}