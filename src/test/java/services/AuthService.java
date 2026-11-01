package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.Getter;
import model.UsuarioModel;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class AuthService {

    private UsuarioModel usuarioModel = new UsuarioModel();

    public final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public Response response;

    private final String baseUrl = "http://localhost:8080";

    private final String schemasPath = "src/test/resources/schemas/";

    private JSONObject jsonSchema;

    // Retorna o token armazenado
    @Getter
    private String token;

    private final ObjectMapper mapper = new ObjectMapper();

    // Carrega o contrato JSON Schema
    private JSONObject loadJsonFromFile(String filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            JSONTokener tokener = new JSONTokener(inputStream);
            return new JSONObject(tokener);
        }
    }

    // Seleciona o contrato esperado
    public void setContract(String contract) throws IOException {

        switch (contract) {

            case "Cadastro bem-sucedido de usuario" ->
                    jsonSchema = loadJsonFromFile(
                            schemasPath + "cadastro-bem-sucedido-de-usuario.json"
                    );

            case "Autenticacao bem-sucedida de usuario" ->
                    jsonSchema = loadJsonFromFile(
                            schemasPath + "autenticacao-bem-sucedida-de-usuario.json"
                    );

            case "Consulta bem sucedida de usuario autenticado" ->
                    jsonSchema = loadJsonFromFile(
                            schemasPath + "consulta_bem_sucedida_de_usuario_autenticado.json"
                    );

            default -> throw new IllegalStateException(
                    "Unexpected contract: " + contract
            );
        }
    }

    // Valida a resposta contra o contrato selecionado
    public Set<ValidationMessage> validateResponseAgainstSchema() throws IOException {

        JSONObject jsonResponse = new JSONObject(response.getBody().asString());

        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(
                SpecVersion.VersionFlag.V4
        );

        JsonSchema schema = schemaFactory.getSchema(jsonSchema.toString());

        JsonNode jsonResponseNode = mapper.readTree(jsonResponse.toString());

        return schema.validate(jsonResponseNode);
    }

    // Limpa os dados de cadastro antes de cada cenário
    public void resetUsuario() {
        usuarioModel = new UsuarioModel();
    }

    // Preenche os dados de cadastro
    public void setFieldsUsuario(String field, String value) {

        switch (field) {
            case "name" -> usuarioModel.setName(value);
            case "email" -> usuarioModel.setEmail(value);
            case "password" -> usuarioModel.setPassword(value);
            case "role" -> usuarioModel.setRole(value);
            case "company_cnpj" -> usuarioModel.setCompanyCnpj(value);
            case "department" -> usuarioModel.setDepartment(value);

            default -> throw new IllegalStateException(
                    "Unexpected field: " + field
            );
        }
    }

    // Envia requisição POST de cadastro
    public void createUsuario(String endPoint) {

        String url = baseUrl + endPoint;

        String bodyToSend = gson.toJson(usuarioModel);

        response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bodyToSend)
                .when()
                .post(url)
                .then()
                .extract()
                .response();
    }

    // Envia requisição POST de login
    public void signIn(String endPoint, Map<String, String> loginData) {

        String url = baseUrl + endPoint;

        String bodyToSend = gson.toJson(loginData);

        response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bodyToSend)
                .when()
                .post(url)
                .then()
                .extract()
                .response();

        // Armazena o JWT quando o login é bem-sucedido
        if (response.statusCode() == 200) {
            token = response.jsonPath().getString("token");
        }
    }

    // Limpa o token armazenado
    public void clearToken() {
        token = null;
    }

    // Consulta os dados do usuário autenticado
    public void getMe(String endPoint, String authToken) {

        String url = baseUrl + endPoint;

        var request = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        // Adiciona o token somente quando ele foi informado
        if (authToken != null && !authToken.isBlank()) {
            request.header("Authorization", "Bearer " + authToken);
        }

        response = request
                .when()
                .get(url)
                .then()
                .extract()
                .response();
    }
}