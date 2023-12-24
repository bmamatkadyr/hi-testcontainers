package testcontainers.pizza;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PizzaRepo extends JpaRepository<Pizza, UUID> {
}
