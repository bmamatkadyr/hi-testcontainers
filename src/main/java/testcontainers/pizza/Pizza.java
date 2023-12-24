package testcontainers.pizza;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pizza {

    @Id
    private UUID id;
    private String name;
    private String description;
    private Double price;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();
    }
}
