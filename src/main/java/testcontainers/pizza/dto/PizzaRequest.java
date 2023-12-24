package testcontainers.pizza.dto;

import testcontainers.pizza.Pizza;

public record PizzaRequest(
        String name,
        String description,
        Double price
) {

    public Pizza buildPizza() {
        return new Pizza(null, name, description, price);
    }

    public void update(Pizza pizza) {
        pizza.setName(name);
        pizza.setDescription(description);
        pizza.setPrice(price);
    }
}
