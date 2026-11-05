package br.com.desafio.sicredi.client;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class UsersClient {

    private static final String BASE_URI = "https://dummyjson.com";
    private static final String USERS = "/users";
    private static final int ALL = 0;

    /** GET /users - primeira página de usuários. */
    public Response list() {
        return given()
                .baseUri(BASE_URI)
                .when().get(USERS);
    }

    /** GET /users?limit=0 - todos os usuários. */
    public Response listAll() {
        return given()
                .baseUri(BASE_URI)
                .queryParam("limit", ALL)
                .when().get(USERS);
    }
}
