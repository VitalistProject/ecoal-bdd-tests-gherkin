
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

        // Busca as metas da empresa autenticada.
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

        java.util.List<Integer> goalIds =
                response.jsonPath().getList("id", Integer.class);

        if (goalIds == null || goalIds.isEmpty()) {
            throw new AssertionError("Não existem metas cadastradas para os testes.");
        }

        // Procura uma meta que ainda permita cadastrar tarefas.
        for (Integer goalId : goalIds) {

            response = given()
                    .baseUri(baseUrl)
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/api/goals/" + goalId);

            if (response.statusCode() != 200) {
                continue;
            }

            java.util.List<?> tasks =
                    response.jsonPath().getList("tasks");

            int taskCount = tasks == null ? 0 : tasks.size();

            if (taskCount < 10) {
                return goalId.longValue();
            }
        }

        throw new AssertionError(
                "Todas as metas cadastradas já possuem 10 tarefas. "
                        + "É necessário liberar espaço em alguma meta para executar os testes."
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
}
