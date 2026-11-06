package br.com.desafio.dummyjson.tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe base dos testes: concentra a configuração do RestAssured
 * e a leitura das credenciais do arquivo credentials.properties.
 */
abstract class BaseTest {

    private static final String BASE_URI = "https://dummyjson.com";
    private static final String CREDENTIALS_FILE = "credentials.properties";

    /** Tempo máximo (ms) para conectar e para aguardar resposta, evitando testes "travados". */
    private static final int TIMEOUT_MS = 10_000;

    protected static RequestSpecification requestSpec;
    protected static String username;
    protected static String password;

    @BeforeAll
    static void setUp() {
        Properties credentials = readCredentialsFile();
        username = required(credentials, "dummyjson.username");
        password = required(credentials, "dummyjson.password");

        RestAssuredConfig config = RestAssuredConfig.config().httpClient(
                HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", TIMEOUT_MS)
                        .setParam("http.socket.timeout", TIMEOUT_MS));

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(config)
                .addFilter(new AllureRestAssured()) // anexa request/response no relatório
                .build();

        // Em caso de falha, imprime request e response no console para facilitar a análise
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    /** Se o arquivo não existir, devolve vazio e o required() explica o que configurar. */
    private static Properties readCredentialsFile() {
        Properties properties = new Properties();
        try (InputStream file = BaseTest.class.getClassLoader().getResourceAsStream(CREDENTIALS_FILE)) {
            if (file != null) {
                properties.load(file);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler " + CREDENTIALS_FILE, e);
        }
        return properties;
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Propriedade '" + key + "' não configurada. Crie src/test/resources/"
                    + CREDENTIALS_FILE + " a partir do arquivo .example");
        }
        return value;
    }
}
