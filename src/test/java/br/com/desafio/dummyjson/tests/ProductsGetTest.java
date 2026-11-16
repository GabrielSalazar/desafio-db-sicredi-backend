package br.com.desafio.dummyjson.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

@DisplayName("GET /products")
class ProductsGetTest extends BaseTest {

    private static final String PRODUCTS = "/products";
    private static final String PRODUCT_BY_ID = "/products/{id}";
    private static final String SEARCH = "/products/search";

    private static final int PAGE_SIZE = 5;
    private static final int SKIP = 10;
    private static final int EXISTING_ID = 1;
    private static final int NON_EXISTING_ID = 99_999;

    @Test
    @DisplayName("Lista produtos respeitando a paginação (limit/skip)")
    void shouldListProductsWithPagination() {
        given().spec(requestSpec)
                .queryParam("limit", PAGE_SIZE)
                .queryParam("skip", SKIP)
                .when().get(PRODUCTS)
                .then()
                .statusCode(200)
                .body("products", hasSize(PAGE_SIZE))
                .body("limit", equalTo(PAGE_SIZE))
                .body("skip", equalTo(SKIP))
                .body("total", greaterThan(PAGE_SIZE))
                // com skip=10, o primeiro item da página é o produto de id 11
                .body("products[0].id", equalTo(SKIP + 1));
    }

    @Test
    @DisplayName("Busca um produto existente pelo id")
    void shouldFindProductById() {
        given().spec(requestSpec)
                .pathParam("id", EXISTING_ID)
                .when().get(PRODUCT_BY_ID)
                .then()
                .statusCode(200)
                .body("id", equalTo(EXISTING_ID))
                .body("title", not(emptyOrNullString()))
                .body("price", greaterThan(0f)); // JsonPath lê decimais como float
    }

    @Test
    @DisplayName("Busca por texto retorna apenas produtos relacionados ao termo")
    void shouldSearchProducts() {
        String term = "phone";

        given().spec(requestSpec)
                .queryParam("q", term)
                .when().get(SEARCH)
                .then()
                .statusCode(200)
                .body("products", not(empty()))
                // filtro Groovy: sobra só quem NÃO tem o termo no título nem na descrição, e isso deve ser vazio
                .body("products.findAll { !(it.title.toLowerCase().contains('" + term + "') "
                        + "|| it.description.toLowerCase().contains('" + term + "')) }", empty());
    }

    @Test
    @DisplayName("Retorna 404 com mensagem para produto inexistente")
    void shouldReturnNotFoundForUnknownId() {
        given().spec(requestSpec)
                .pathParam("id", NON_EXISTING_ID)
                .when().get(PRODUCT_BY_ID)
                .then()
                .statusCode(404)
                .body("message", equalTo("Product with id '" + NON_EXISTING_ID + "' not found"));
    }
}
