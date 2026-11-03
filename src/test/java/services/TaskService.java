
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
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class TaskService {

    private final String baseUrl = "http://localhost:8080";
    private final String schemasPath = "src/test/resources/schemas/";

    private String token;
    private JSONObject jsonSchema;

    public Response response;

    private final ObjectMapper mapper = new ObjectMapper();

    public void setToken(String token) {
        this.token = token;
    }

    private JSONObject loadJsonFromFile(String filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            return new JSONObject(new JSONTokener(inputStream));
        }
    }

    public void setContract(String contract) throws IOException {
        switch (contract) {
            case "Consulta de uma tarefa":
            case "Cadastro de tarefa":
            case "Atualização de tarefa":
                jsonSchema = loadJsonFromFile(schemasPath + "tarefa.json");
                break;

            case "Listagem de tarefas":
                jsonSchema = loadJsonFromFile(schemasPath + "lista-tarefas.json");
                break;

            default:
                throw new IllegalStateException("Contrato inesperado: " + contract);
        }
    }

    public Set<ValidationMessage> validateResponseAgainstSchema() throws IOException {
        JsonSchemaFactory schemaFactory =
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);

        JsonSchema schema = schemaFactory.getSchema(jsonSchema.toString());

        JsonNode responseNode =
                mapper.readTree(response.getBody().asString());

        return schema.validate(responseNode);
    }


    public Long createGoalFixture() {
        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/goals");

        if (response.statusCode() != 200) {
            throw new AssertionError(
                    "Não foi possível consultar as metas: " + response.asString()
            );
        }

        java.util.List<Map<String, Object>> goals =
                response.jsonPath().getList("$");

        if (goals == null) {
            throw new AssertionError("A lista de metas não foi retornada.");
        }

        String[] dimensions = {
                "ENVIRONMENTAL",
                "SOCIAL",
                "GOVERNANCE"
        };

        for (String dimension : dimensions) {
            long count = goals.stream()
                    .filter(goal -> goal.get("dimension") != null)
                    .filter(goal -> dimension.equalsIgnoreCase(
                            goal.get("dimension").toString()
                    ))
                    .count();

            if (count >= 10) {
                continue;
            }

            Map<String, String> goalData = Map.of(
                    "title", "Meta temporária BDD " + java.util.UUID.randomUUID(),
                    "description", "Meta criada automaticamente para testes",
                    "dimension", dimension
            );

            response = given()
                    .baseUri(baseUrl)
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(goalData)
                    .when()
                    .post("/api/goals");

            if (response.statusCode() == 201) {
                return response.jsonPath().getLong("id");
            }

            if (response.statusCode() != 422) {
                throw new AssertionError(
                        "Não foi possível criar a meta temporária: "
                                + response.asString()
                );
            }
        }

        throw new AssertionError(
                "Todas as dimensões já possuem 10 metas. "
                        + "Não foi possível criar uma meta temporária."
        );
    }


    public void createTask(
            String endpoint,
            Long goalId,
            String title,
            String description,
            String score
    ) {
        Map<String, Object> taskData = Map.of(
                "title", title,
                "description", description,
                "score", Double.parseDouble(score)
        );

        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(taskData)
                .when()
                .post(endpoint + "/" + goalId + "/tasks");
    }

    public void getTasks(String endpoint, Long goalId) {
        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint + "/" + goalId + "/tasks");
    }

    public void getTasksByCompleted(
            String endpoint,
            Long goalId,
            Boolean completed
    ) {

        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .queryParam("completed", completed)
                .when()
                .get(endpoint + "/" + goalId + "/tasks");
    }

    public void getTasksByDate(
            String endpoint,
            Long goalId,
            String createdAt
    ) {
        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .queryParam("created_at", createdAt)
                .when()
                .get(endpoint + "/" + goalId + "/tasks");
    }

    public void getTasksByFilters(
            String endpoint,
            Long goalId,
            Boolean completed,
            String createdAt
    ) {
        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .queryParam("completed", completed)
                .queryParam("created_at", createdAt)
                .when()
                .get(endpoint + "/" + goalId + "/tasks");
    }

    public void getTask(String endpoint, Long goalId, Long taskId) {
        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint + "/" + goalId + "/tasks/" + taskId);
    }

    public void updateTask(
            String endpoint,
            Long goalId,
            Long taskId,
            String title,
            String description,
            String score,
            Boolean completed
    ) {

        Map<String, Object> taskData = Map.of(
                "title", title,
                "description", description,
                "score", Double.parseDouble(score),
                "completed", completed
        );

        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(taskData)
                .when()
                .put(endpoint + "/" + goalId + "/tasks/" + taskId);
    }

    public void deleteTask(String endpoint, Long goalId, Long taskId) {
        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(endpoint + "/" + goalId + "/tasks/" + taskId);
    }

    public void deleteGoal(String endpoint, Long goalId) {
        response = given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(endpoint + "/" + goalId);
    }
}
