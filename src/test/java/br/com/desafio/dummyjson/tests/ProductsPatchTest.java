package br.com.desafio.dummyjson.tests;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("PATCH /products/{id}")
class ProductsPatchTest extends BaseTest {

    private static final String PRODUCT_BY_ID = "/products/{id}";
    private static final int PRODUCT_ID = 1;

    @Test
    @DisplayName("Altera somente o título e mantém os demais campos")
    void shouldUpdateOnlyTitle() {
        String newTitle = "Título alterado via PATCH";

        // Arrange: estado atual, para comparar o que NÃO deveria mudar
        JsonPath original = given().spec(requestSpec)
                .pathParam("id", PRODUCT_ID)
                .when().get(PRODUCT_BY_ID)
                .then()
                .statusCode(200)
                .extract().jsonPath();

        // Act + Assert: envia apenas o título; só ele muda
        given().spec(requestSpec)
                .pathParam("id", PRODUCT_ID)
                .body(Map.of("title", newTitle))
                .when().patch(PRODUCT_BY_ID)
                .then()
                .statusCode(200)
                .body("title", equalTo(newTitle))
                .body("id", equalTo(PRODUCT_ID))
                .body("description", equalTo(original.getString("description")))
                .body("price", equalTo(original.getFloat("price")))
                .body("category", equalTo(original.getString("category")));
    }
}
