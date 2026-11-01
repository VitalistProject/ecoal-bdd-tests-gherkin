
package steps;

import com.networknt.schema.ValidationMessage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import org.junit.Assert;
import services.AuthService;
import services.TaskService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class TaskSteps {

    private final AuthService authService = new AuthService();
    private final TaskService taskService = new TaskService();

    private Long goalId;
    private Long taskId;

    @Dado("que eu esteja autenticado para gerenciar tarefas")
    public void autenticarParaGerenciarTarefas() {

        Map<String, String> credentials = Map.of(
                "email", "alice@example.com",
                "password", "password"
        );

        authService.signIn("/api/auth/sign-in", credentials);

        Assert.assertEquals(
                "Não foi possível autenticar o usuário",
                200,
                authService.response.statusCode()
        );

        String token = authService.getToken();

        Assert.assertNotNull("O token JWT não foi retornado", token);

        taskService.setToken(token);
    }

    @Dado("que exista uma meta temporária para os testes de tarefas")
    public void criarMetaTemporaria() {

        goalId = taskService.createGoalFixture();

        Assert.assertNotNull(
                "O ID da meta temporária não foi retornado",
                goalId
        );
    }

    @Dado("que exista uma tarefa cadastrada nessa meta")
    public void criarTarefaTemporaria() {

        taskService.createTask(
                "/api/goals",
                goalId,
                "Tarefa temporária BDD",
                "Tarefa criada automaticamente para os testes",
                "10.0"
        );

        Assert.assertEquals(
                "Não foi possível criar a tarefa temporária: "
                        + taskService.response.asString(),
                201,
                taskService.response.statusCode()
        );

        taskId = taskService.response.jsonPath().getLong("id");
    }

    @Quando("eu cadastrar uma tarefa no endpoint {string} com os seguintes dados:")
    public void cadastrarTarefa(String endpoint, DataTable dataTable) {

        Map<String, String> data =
                dataTable.asMap(String.class, String.class);

        taskService.createTask(
                endpoint,
                goalId,
                data.get("title"),
                data.get("description"),
                data.get("score")
        );

        if (taskService.response.statusCode() == 201) {
            taskId = taskService.response.jsonPath().getLong("id");
        }
    }

    @Quando("eu cadastrar uma tarefa sem título no endpoint {string}")
    public void cadastrarTarefaSemTitulo(String endpoint) {

        taskService.createTask(
                endpoint,
                goalId,
                "",
                "Tarefa sem título",
                "10.0"
        );
    }

    @Quando("eu consultar as tarefas no endpoint {string}")
    public void consultarTarefas(String endpoint) {
        taskService.getTasks(endpoint, goalId);
    }

    @Quando("eu consultar as tarefas concluídas no endpoint {string}")
    public void consultarTarefasConcluidas(String endpoint) {
        taskService.getTasksByCompleted(endpoint, goalId, true);
    }

    @Quando("eu consultar as tarefas pendentes no endpoint {string}")
    public void consultarTarefasPendentes(String endpoint) {
        taskService.getTasksByCompleted(endpoint, goalId, false);
    }

    @Quando("eu consultar as tarefas criadas a partir de {string} no endpoint {string}")
    public void consultarTarefasPorData(String createdAt, String endpoint) {
        taskService.getTasksByDate(endpoint, goalId, createdAt);
    }

    @Quando("eu consultar as tarefas concluídas criadas a partir de {string} no endpoint {string}")
    public void consultarTarefasPorFiltros(String createdAt, String endpoint) {
        taskService.getTasksByFilters(endpoint, goalId, true, createdAt);
    }

    @Quando("eu consultar essa tarefa no endpoint {string}")
    public void consultarTarefa(String endpoint) {
        taskService.getTask(endpoint, goalId, taskId);
    }

    @Quando("eu consultar uma tarefa inexistente no endpoint {string}")
    public void consultarTarefaInexistente(String endpoint) {
        taskService.getTask(endpoint, goalId, 999999L);
    }

    @Quando("eu atualizar essa tarefa no endpoint {string}")
    public void atualizarTarefa(String endpoint) {

        taskService.updateTask(
                endpoint,
                goalId,
                taskId,
                "Tarefa BDD atualizada",
                "Descrição atualizada pelos testes",
                "20.0",
                true
        );
    }

    @Quando("eu excluir essa tarefa no endpoint {string}")
    public void excluirTarefa(String endpoint) {
        taskService.deleteTask(endpoint, goalId, taskId);
    }

    @Então("o status da resposta da tarefa deve ser {int}")
    public void verificarStatus(int statusCode) {

        Assert.assertNotNull(
                "Nenhuma resposta foi recebida da API de tarefas",
                taskService.response
        );

        Assert.assertEquals(
                "O status HTTP retornado não é o esperado. Resposta: "
                        + taskService.response.asString(),
                statusCode,
                taskService.response.statusCode()
        );
    }

    @Então("a resposta da tarefa deve possuir os campos obrigatórios")
    public void verificarCamposObrigatorios() {

        Assert.assertNotNull(
                taskService.response.jsonPath().get("id")
        );

        Assert.assertNotNull(
                taskService.response.jsonPath().get("title")
        );

        Assert.assertNotNull(
                taskService.response.jsonPath().get("completed")
        );

        Assert.assertNotNull(
                taskService.response.jsonPath().get("goal_id")
        );

        Assert.assertNotNull(
                taskService.response.jsonPath().get("created_at")
        );

        Assert.assertNotNull(
                taskService.response.jsonPath().get("updated_at")
        );
    }

    @Então("a resposta da tarefa deve respeitar o contrato {string}")
    public void validarContrato(String contrato) throws IOException {

        taskService.setContract(contrato);

        Set<ValidationMessage> erros =
                taskService.validateResponseAgainstSchema();

        Assert.assertTrue(
                "A resposta não respeita o contrato JSON Schema: " + erros,
                erros.isEmpty()
        );
    }

    @Então("a resposta da tarefa não deve possuir conteúdo")
    public void verificarRespostaVazia() {

        Assert.assertEquals(
                "",
                taskService.response.getBody().asString()
        );
    }
}
