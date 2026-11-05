package br.com.desafio.sicredi.tests;

import br.com.desafio.sicredi.client.StatusClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("GET /test - status da aplicação")
class StatusTest {

    private final StatusClient client = new StatusClient();

    @Test
    @DisplayName("CT-01 - HealthCheck - Aplicação responde 200 com status 'ok'")
    void shouldReturnApplicationStatus() {
        client.status()
                .then()
                .statusCode(200)
                .body("status", equalTo("ok"))
                .body("method", equalTo("GET"));
    }
}
