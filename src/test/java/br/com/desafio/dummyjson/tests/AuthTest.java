package br.com.desafio.dummyjson.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Fluxo de autenticação: login -> obtenção do token -> acesso a recurso protegido,
 * além dos cenários de erro.
 */
@DisplayName("Autenticação")
class AuthTest extends BaseTest {

    private static final String LOGIN = "/auth/login";
    private static final String ME = "/auth/me";

    @Test
    @DisplayName("Login com credenciais válidas retorna tokens e dados do usuário")
    void shouldLoginWithValidCredentials() {
        login(username, password)
                .then()
                .statusCode(200)
                .body("username", equalTo(username))
                .body("accessToken", not(emptyOrNullString()))
                .body("refreshToken", not(emptyOrNullString()))
                .body("$", not(hasKey("password"))); // a senha nunca deve voltar na resposta
    }

    @Test
    @DisplayName("Login com senha errada é recusado")
    void shouldRejectInvalidPassword() {
        login(username, "senha-errada")
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid credentials"))
                .body("$", not(hasKey("accessToken")));
    }

    @Test
    @DisplayName("Token válido dá acesso ao recurso protegido /auth/me")
    void shouldAccessProtectedResourceWithToken() {
        String token = login(username, password)
                .then()
                .statusCode(200)
                .extract().path("accessToken");

        // O recurso protegido devolve o próprio usuário logado
        given().spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when().get(ME)
                .then()
                .statusCode(200)
                .body("username", equalTo(username))
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Acesso ao recurso protegido sem token é negado")
    void shouldDenyAccessWithoutToken() {
        given().spec(requestSpec)
                .when().get(ME)
                .then()
                .statusCode(401)
                .body("message", equalTo("Access Token is required"));
    }

    @Test
    @DisplayName("Acesso ao recurso protegido com token inválido é negado")
    void shouldDenyAccessWithInvalidToken() {
        given().spec(requestSpec)
                .header("Authorization", "Bearer token-invalido")
                .when().get(ME)
                .then()
                .statusCode(401)
                .body("message", containsString("Invalid"));
    }

    /** POST /auth/login - devolve a resposta crua, para testar sucesso e erro. */
    private Response login(String user, String pass) {
        return given().spec(requestSpec)
                .body(Map.of("username", user, "password", pass))
                .when().post(LOGIN);
    }
}
