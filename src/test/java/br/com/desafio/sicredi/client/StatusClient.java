package br.com.desafio.sicredi.client;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

/** Client do endpoint de status da aplicação. */
public class StatusClient {

    private static final String BASE_URI = "https://dummyjson.com";
    private static final String TEST = "/test";

    /** GET /test - status da aplicação. */
    public Response status() {
        return given()
                .baseUri(BASE_URI)
                .when().get(TEST);
    }
}
