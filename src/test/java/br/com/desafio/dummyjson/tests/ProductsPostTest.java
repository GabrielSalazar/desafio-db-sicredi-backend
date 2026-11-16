package br.com.desafio.dummyjson.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("POST /products/add")
class ProductsPostTest extends BaseTest {

    private static final String ADD = "/products/add";

    @Test
    @DisplayName("Cadastra um produto com status 201 e devolve os dados enviados com id gerado")
    void shouldCreateProduct() {
        String title = "Teclado mecânico";
        String description = "Switch azul, ABNT2";
        float price = 349.90f; // float de propósito: o JsonPath lê decimais da resposta como float
        String category = "accessories";

        // A API é simulada: o produto não é persistido, então não buscamos ele depois
        given().spec(requestSpec)
                .body(Map.of(
                        "title", title,
                        "description", description,
                        "price", price,
                        "category", category))
                .when().post(ADD)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(title))
                .body("description", equalTo(description))
                .body("price", equalTo(price))
                .body("category", equalTo(category));
    }
}
