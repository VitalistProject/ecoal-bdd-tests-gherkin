
package steps;

import com.networknt.schema.ValidationMessage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import org.junit.Assert;
import services.AuthService;
import services.GoalService;

import io.cucumber.java.After;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class GoalSteps {
    private final AuthService authService = new AuthService();
    private final GoalService goalService = new GoalService();

    private boolean createdGoalInScenario = false;

    private Long goalId;

    @Dado("que eu esteja autenticado para gerenciar metas")
    public void autenticarParaGerenciarMetas() {
        Map<String, String> credentials = Map.of(
                "email", "bob@example.com",
                "password", "password"
        );
        authService.signIn("/api/auth/sign-in", credentials);

        Assert.assertEquals("Não foi possível autenticar o usuário",
                200, authService.response.statusCode());

        String token = authService.getToken();

        Assert.assertNotNull(
                "O token JWT não foi retornado",
                token
        );

        goalService.setToken(token);
    }

    @Quando("eu cadastrar uma meta com os seguintes dados no endpoint {string}:")
    public void cadastrarMeta(String endpoint, DataTable dataTable) {
        Map<String, String> data =
                dataTable.asMap(String.class, String.class);

        goalService.createGoal(
                endpoint,
                data.get("title"),
                data.get("description"),
                data.get("dimension")
        );

        if (goalService.response.statusCode() == 201) {
            goalId = goalService.response.jsonPath().getLong("id");
            createdGoalInScenario = true;
        }
    }

    @Quando("eu cadastrar uma meta sem informar o título no endpoint {string}")
    public void cadastrarMetaSemTitulo(String endpoint) {
        goalService.createGoal(
                endpoint,
                "",
                "Meta criada para teste",
                "ENVIRONMENTAL"
        );
    }

    @Quando("eu consultar a lista de metas no endpoint {string}")
    public void consultarListaDeMetas(String endpoint) {
        goalService.getGoals(endpoint);
    }

    @Quando("eu consultar as metas da dimensão {string} no endpoint {string}")
    public void consultarMetasPorDimensao(
            String dimension,
            String endpoint) {
        goalService.getGoalsByDimension(
                endpoint,
                dimension
        );
    }

    @Quando("eu consultar as metas criadas a partir de {string} no endpoint {string}")
    public void consultarMetasPorData(
            String createdAt,
            String endpoint) {
        goalService.getGoalsByDate(
                endpoint,
                createdAt
        );
    }

    @Dado("que exista uma meta cadastrada")
    public void queExistaUmaMetaCadastrada() {
        String titulo = "Meta BDD " + java.util.UUID.randomUUID();

        goalService.createGoal(
                "/api/goals",
                titulo,
                "Meta temporária criada para teste automatizado",
                "SOCIAL"
        );

        Assert.assertEquals(
                "Não foi possível criar a meta temporária",
                201,
                goalService.response.statusCode()
        );

        goalId = goalService.response.jsonPath().getLong("id");
        createdGoalInScenario = true;
    }

    @Quando("eu consultar essa meta no endpoint {string}")
    public void consultarEssaMeta(String endpoint) {
        goalService.getGoal(endpoint, goalId);
    }

    @Quando("eu consultar uma meta inexistente no endpoint {string}")
    public void consultarMetaInexistente(String endpoint) {
        goalService.getGoal(endpoint, 999999L);
    }

    @Quando("eu atualizar essa meta no endpoint {string}")
    public void atualizarEssaMeta(String endpoint) {
        goalService.updateGoal(
                endpoint,
                goalId,
                "Meta BDD Atualizada",
                "Descrição atualizada pelo teste BDD",
                "ENVIRONMENTAL"
        );
    }

    @Quando("eu excluir essa meta no endpoint {string}")
    public void excluirEssaMeta(String endpoint) {
        goalService.deleteGoal(endpoint, goalId);
    }

    @Então("o status da resposta da meta deve ser {int}")
    public void verificarStatusRespostaMeta(int statusCode) {
        Assert.assertNotNull(
                "Nenhuma resposta foi recebida da API de metas",
                goalService.response
        );

        Assert.assertEquals(
                "O status HTTP retornado não é o esperado",
                statusCode,
                goalService.response.statusCode()
        );
    }

    @Então("a resposta da meta deve possuir os campos obrigatórios")
    public void verificarCamposObrigatorios() {

        Assert.assertNotNull(
                "O ID da meta não foi retornado",
                goalService.response.jsonPath().get("id")
        );

        Assert.assertNotNull(
                "O título da meta não foi retornado",
                goalService.response.jsonPath().get("title")
        );

        Assert.assertNotNull(
                "A dimensão da meta não foi retornada",
                goalService.response.jsonPath().get("dimension")
        );

        Assert.assertNotNull(
                "O ID da empresa não foi retornado",
                goalService.response.jsonPath().get("company_id")
        );

        Assert.assertNotNull(
                "A data de criação não foi retornada",
                goalService.response.jsonPath().get("created_at")
        );

        Assert.assertNotNull(
                "A data de atualização não foi retornada",
                goalService.response.jsonPath().get("updated_at")
        );
    }

    @Então("a resposta da meta deve respeitar o contrato {string}")
    public void validarContratoDaMeta(String contrato)
            throws IOException {

        goalService.setContract(contrato);

        Set<ValidationMessage> erros =
                goalService.validateResponseAgainstSchema();

        Assert.assertTrue(
                "A resposta não respeita o contrato JSON Schema: " + erros,
                erros.isEmpty()
        );
    }

    @Então("a resposta da meta não deve possuir conteúdo")
    public void verificarRespostaSemConteudo() {
        Assert.assertEquals(
                "A resposta deveria estar vazia",
                "",
                goalService.response.getBody().asString()
        );
    }

    @After
    public void limparMetaCriadaPeloTeste() {
        if (createdGoalInScenario && goalId != null) {

            goalService.deleteGoal("/api/goals", goalId);

            if (goalService.response.statusCode() != 204) {
                System.out.println(
                        "AVISO: Não foi possível excluir a meta temporária. Status: "
                                + goalService.response.statusCode()
                );
            }
        }
    }
}
