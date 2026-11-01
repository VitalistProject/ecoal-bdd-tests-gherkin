
package services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;

public class CompanyService {

    private final String baseUrl = "http://localhost:8080";
    private String token;
    private Long companyId = 1L;

    public Response response;

    public void setToken(String token) {
        this.token = token;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    private JSONObject companyData(
            String name,
            String cnpj,
            Double globalScoreGoal) {

        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("cnpj", cnpj);

        if (globalScoreGoal != null) {
            json.put("global_score_goal", globalScoreGoal);
        }

        return json;
    }

    public void createCompany(
            String endPoint,
            String name,
            String cnpj,
            Double globalScoreGoal) {

        response = given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(companyData(name, cnpj, globalScoreGoal).toString())
                .when()
                .post(endPoint)
                .then()
                .extract()
                .response();

        if (response.statusCode() == 201) {
            companyId = response.jsonPath().getLong("id");
        }
    }

    public void getCompany(String endPoint, Long id) {
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

    public void updateCompany(
            String endPoint,
            Long id,
            String name,
            String cnpj,
            Double globalScoreGoal) {

        response = given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(companyData(name, cnpj, globalScoreGoal).toString())
                .when()
                .put(endPoint + "/" + id)
                .then()
                .extract()
                .response();
    }

    public void deleteCompany(String endPoint, Long id) {
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
