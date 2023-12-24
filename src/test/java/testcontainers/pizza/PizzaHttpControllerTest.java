package testcontainers.pizza;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import testcontainers.pizza.dto.PizzaRequest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class PizzaHttpControllerTest {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Autowired
    PizzaRepo pizzaRepo;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:%d".formatted(port);
        pizzaRepo.deleteAll();
    }

    @Test
    void shouldGetAllPizzas() {

        var pizzaList = List.of(
                new Pizza(null, "Margherita", "Tomato sauce, mozzarella", 5.0),
                new Pizza(null, "Diavola", "Tomato sauce, mozzarella, spicy salami", 7.5),
                new Pizza(null, "Quattro Formaggi", "Tomato sauce, mozzarella, parmesan, gorgonzola, artichokes", 8.5)
        );

        pizzaRepo.saveAll(pizzaList);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/pizza/all")
                .then()
                .statusCode(200)
                .body(".", hasSize(3));
    }

    @Test
    void shouldAddPizza() {

        var pizzaRequest = new PizzaRequest("Margherita", "Tomato sauce, mozzarella", 5.0);

        given()
                .contentType(ContentType.JSON)
                .body(pizzaRequest)
                .when()
                .post("/api/pizza/add")
                .then()
                .statusCode(200)
                .body("name", equalTo(pizzaRequest.name()))
                .body("description", equalTo(pizzaRequest.description()))
                .body("price", equalTo(pizzaRequest.price().floatValue()));
    }

    @Test
    void shouldFindPizza() {

        var pizza = new Pizza(null, "Margherita", "Tomato sauce, mozzarella", 5.0);

        pizzaRepo.save(pizza);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/pizza/find/%s".formatted(pizza.getId()))
                .then()
                .statusCode(200)
                .body("name", equalTo(pizza.getName()))
                .body("description", equalTo(pizza.getDescription()))
                .body("price", equalTo(pizza.getPrice().floatValue()));
    }

    @Test
    void shouldDeletePizza() {

        var pizza = new Pizza(null, "Margherita", "Tomato sauce, mozzarella", 5.0);

        var savedPizza = pizzaRepo.save(pizza);

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/pizza/delete/%s".formatted(savedPizza.getId()))
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/pizza/all")
                .then()
                .statusCode(200)
                .body(".", hasSize(0));
    }

    @Test
    void shouldUpdatePizza() {

        var pizza = new Pizza(null, "Margherita", "Tomato sauce, mozzarella", 5.0);

        var savedPizza = pizzaRepo.save(pizza);

        var pizzaRequest = new PizzaRequest("Diavola", "Tomato sauce, mozzarella, spicy salami", 7.5);

        given()
                .contentType(ContentType.JSON)
                .body(pizzaRequest)
                .when()
                .put("/api/pizza/update/%s".formatted(savedPizza.getId()))
                .then()
                .statusCode(200)
                .body("name", equalTo(pizzaRequest.name()))
                .body("description", equalTo(pizzaRequest.description()))
                .body("price", equalTo(pizzaRequest.price().floatValue()));
    }
}