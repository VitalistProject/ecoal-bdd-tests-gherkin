package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class AnalyticsService {
    private String token;

    private final String baseUrl = "http://localhost:8080";
    private final String schemasPath = "src/test/resources/schemas/";

    public Response response;

    private JSONObject jsonSchema;
    private final ObjectMapper mapper = new ObjectMapper();

    private JSONObject loadJsonFromFile(String filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            JSONTokener tokener = new JSONTokener(inputStream);
            return new JSONObject(tokener);
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setContract(String contract) throws IOException {
        switch (contract) {
            case "Consulta de progresso das dimensões" ->
                    jsonSchema = loadJsonFromFile(
                            schemasPath + "consulta-progresso-dimensoes.json"
                    );

            case "Consulta de pontuação geral" ->
                    jsonSchema = loadJsonFromFile(
                            schemasPath + "consulta-pontuacao-geral.json"
                    );

            case "Consulta dos dados do dashboard" ->
                    jsonSchema = loadJsonFromFile(
                            schemasPath + "consulta-dashboard.json"
                    );

            default -> throw new IllegalStateException(
                    "Contrato inesperado: " + contract
            );
        }
    }

    public Set<ValidationMessage> validateResponseAgainstSchema() throws IOException {
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());

        JsonSchemaFactory schemaFactory =
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);

        JsonSchema schema = schemaFactory.getSchema(jsonSchema.toString());

        JsonNode jsonResponseNode = mapper.readTree(jsonResponse.toString());

        return schema.validate(jsonResponseNode);
    }

    public void getAnalytics(String endPoint) {
        response = given()
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(baseUrl + endPoint)
                .then()
                .extract()
                .response();
    }

    public void getAnalytics(String endPoint, String period) {
        response = given()
                .accept(ContentType.JSON)
                .queryParam("period", period)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(baseUrl + endPoint)
                .then()
                .extract()
                .response();
    }
}