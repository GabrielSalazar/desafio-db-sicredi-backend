package br.com.desafio.dummyjson.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("PUT /products/{id}")
class ProductsPutTest extends BaseTest {

    private static final String PRODUCT_BY_ID = "/products/{id}";
    private static final int PRODUCT_ID = 1;

    @Test
    @DisplayName("Substitui o produto e devolve os campos enviados")
    void shouldReplaceProduct() {
        String title = "Produto substituído";
        String description = "Descrição substituída";
        // preço com casas decimais de propósito: a API devolve "10.0" como inteiro "10",
        // o que quebraria a comparação de tipo no JsonPath
        float price = 19.9f;
        String category = "groceries";

        // Observação: a API mantém na resposta os campos NÃO enviados (comportamento de PATCH).
        // Isso está registrado como BUG-01 no README; aqui validamos só os campos enviados.
        given().spec(requestSpec)
                .pathParam("id", PRODUCT_ID)
                .body(Map.of(
                        "title", title,
                        "description", description,
                        "price", price,
                        "category", category))
                .when().put(PRODUCT_BY_ID)
                .then()
                .statusCode(200)
                .body("id", equalTo(PRODUCT_ID))
                .body("title", equalTo(title))
                .body("description", equalTo(description))
                .body("price", equalTo(price))
                .body("category", equalTo(category));
    }
}
