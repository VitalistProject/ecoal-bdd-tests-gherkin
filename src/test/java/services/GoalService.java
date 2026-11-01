
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

public class GoalService {

    private final String baseUrl = "http://localhost:8080";
    private final String schemasPath = "src/test/resources/schemas/";

    private String token;

    public Response response;

    private JSONObject jsonSchema;

    private final ObjectMapper mapper = new ObjectMapper();

    private JSONObject loadJsonFromFile(String filePath)
            throws IOException {

        try (InputStream inputStream =
                     Files.newInputStream(Paths.get(filePath))) {

            JSONTokener tokener = new JSONTokener(inputStream);
            return new JSONObject(tokener);
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setContract(String contract) throws IOException {

        switch (contract) {

            case "Consulta de uma meta", "Cadastro de meta", "Atualização de meta":
                jsonSchema = loadJsonFromFile(
                        schemasPath + "meta.json"
                );
                break;

            case "Listagem de metas":
                jsonSchema = loadJsonFromFile(
                        schemasPath + "lista-metas.json"
                );
                break;

            default:
                throw new IllegalStateException(
                        "Contrato inesperado: " + contract
                );
        }
    }

    public Set<ValidationMessage> validateResponseAgainstSchema()
            throws IOException {

        if (response == null) {
            throw new IllegalStateException(
                    "Nenhuma resposta foi recebida da API."
            );
        }

        if (jsonSchema == null) {
            throw new IllegalStateException(
                    "Nenhum contrato JSON Schema foi selecionado."
            );
        }

        JsonNode jsonResponseNode =
                mapper.readTree(response.getBody().asString());

        JsonSchemaFactory schemaFactory =
                JsonSchemaFactory.getInstance(
                        SpecVersion.VersionFlag.V4
                );

        JsonSchema schema =
                schemaFactory.getSchema(jsonSchema.toString());

        return schema.validate(jsonResponseNode);
    }

    public void createGoal(
            String endPoint,
            String title,
            String description,
            String dimension) {

        JSONObject body = new JSONObject();

        body.put("title", title);
        body.put("dimension", dimension);

        if (description != null) {
            body.put("description", description);
        }

        response = given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(body.toString())
                .when()
                .post(endPoint)
                .then()
                .extract()
                .response();
    }

    public void getGoals(String endPoint) {

        response = given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endPoint)
                .then()
                .extract()
                .response();
    }

    public void getGoalsByDimension(
            String endPoint,
            String dimension) {

        response = given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .queryParam("dimension", dimension)
                .when()
                .get(endPoint)
                .then()
                .extract()
                .response();
    }

    public void getGoalsByDate(
            String endPoint,
            String createdAt) {

        response = given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .queryParam("created_at", createdAt)
                .when()
                .get(endPoint)
                .then()
                .extract()
                .response();
    }

    public void getGoal(String endPoint, Long id) {

        response = given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endPoint + "/" + id)
                .then()
                .extract()
                .response();
    }

    public void updateGoal(
            String endPoint,
            Long id,
            String title,
            String description,
            String dimension) {

        JSONObject body = new JSONObject();

        body.put("title", title);
        body.put("dimension", dimension);

        if (description != null) {
            body.put("description", description);
        }

        response = given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(body.toString())
                .when()
                .put(endPoint + "/" + id)
                .then()
                .extract()
                .response();
    }

    public void deleteGoal(String endPoint, Long id) {

        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(endPoint + "/" + id)
                .then()
                .extract()
                .response();
    }
}
