package testcontainers.pizza;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import testcontainers.pizza.dto.PizzaRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/pizza")
@RequiredArgsConstructor
public class PizzaHttpController {

    private final PizzaRepo pizzaRepo;

    @GetMapping("/all")
    public Iterable<Pizza> getAllPizzas() {
        return pizzaRepo.findAll();
    }

    @PostMapping("/add")
    public Pizza addPizza(@RequestBody PizzaRequest pizzaRequest) {
        return pizzaRepo.save(pizzaRequest.buildPizza());
    }

    @GetMapping("/find/{id}")
    public Pizza findPizza(@PathVariable("id") UUID id) {
        return pizzaRepo.findById(id).orElseThrow();
    }

    @DeleteMapping("/delete/{id}")
    public void deletePizza(@PathVariable("id") UUID id) {
        pizzaRepo.deleteById(id);
    }

    @PutMapping("/update/{id}")
    public Pizza updatePizza(@PathVariable("id") UUID id, @RequestBody PizzaRequest pizzaRequest) {
        Pizza pizza = pizzaRepo.findById(id).orElseThrow();
        pizzaRequest.update(pizza);
        return pizzaRepo.save(pizza);
    }
}
