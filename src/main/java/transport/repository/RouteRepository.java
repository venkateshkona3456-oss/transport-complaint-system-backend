package transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import transport.entity.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
}