package br.com.desafio.dummyjson.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@DisplayName("DELETE /products/{id}")
class ProductsDeleteTest extends BaseTest {

    private static final String PRODUCT_BY_ID = "/products/{id}";
    private static final int PRODUCT_ID = 1;

    @Test
    @DisplayName("Remove o produto e sinaliza isDeleted com data de remoção")
    void shouldDeleteProduct() {
        given().spec(requestSpec)
                .pathParam("id", PRODUCT_ID)
                .when().delete(PRODUCT_BY_ID)
                .then()
                .statusCode(200)
                .body("id", equalTo(PRODUCT_ID))
                .body("isDeleted", is(true))           // exclusão lógica simulada pela API
                .body("deletedOn", not(emptyOrNullString()));
    }
}
