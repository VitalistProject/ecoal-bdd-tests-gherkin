
package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;

import services.AuthService;
import services.CompanyService;

import java.util.Map;

public class CompanySteps {

    private final AuthService authService = new AuthService();
    private final CompanyService companyService = new CompanyService();

    private static final String COMPANY_ENDPOINT = "/api/companies";

    private Long companyId;

    private String gerarCnpjUnico() {
        long numero = ThreadLocalRandom.current()
                .nextLong(10_000_000_000_000L, 99_999_999_999_999L);

        return String.valueOf(numero);
    }

    // =========================
    // AUTENTICAÇÃO
    // =========================

    @Dado("que eu esteja autenticado para gerenciar empresas")
    public void autenticarParaGerenciarEmpresas() {

        Map<String, String> credentials = Map.of(
                "email", "bob@example.com",
                "password", "password"
        );

        authService.signIn("/api/auth/sign-in", credentials);

        Assert.assertEquals(
                "Não foi possível autenticar o usuário",
                200,
                authService.response.statusCode()
        );

        String token = authService.getToken();

        Assert.assertNotNull(
                "O token JWT não foi retornado",
                token
        );

        companyService.setToken(token);
    }

    // =========================
    // CADASTRO
    // =========================

    @Quando("eu cadastrar uma empresa com os seguintes dados:")
    public void cadastrarEmpresa(DataTable dataTable) {

        Map<String, String> data = dataTable.asMap(
                String.class,
                String.class
        );

        Double globalScoreGoal = data.containsKey("global_score_goal")
                ? Double.parseDouble(data.get("global_score_goal"))
                : null;

        companyService.createCompany(
                COMPANY_ENDPOINT,
                data.get("name"),
                gerarCnpjUnico(),
                globalScoreGoal
        );

        if (companyService.response.statusCode() == 201) {
            companyId = companyService.response.jsonPath().getLong("id");
        }
    }

    @Quando("eu cadastrar uma empresa sem informar o nome")
    public void cadastrarEmpresaSemNome() {

        companyService.createCompany(
                COMPANY_ENDPOINT,
                "",
                gerarCnpjUnico(),
                80.0
        );
    }

    // =========================
    // CONSULTA
    // =========================

    @Dado("que exista uma empresa cadastrada")
    public void queExistaUmaEmpresaCadastrada() {

        // Cria uma empresa exclusiva para o cenário
        String cnpj = gerarCnpjUnico();

        companyService.createCompany(
                COMPANY_ENDPOINT,
                "Empresa BDD Temporaria",
                cnpj,
                80.0
        );

        Assert.assertEquals(
                "Não foi possível criar a empresa temporária",
                201,
                companyService.response.statusCode()
        );

        companyId = companyService.response.jsonPath().getLong("id");

        // Prepara o cadastro de um usuário vinculado à empresa
        authService.resetUsuario();

        authService.setFieldsUsuario("name", "Usuario BDD");
        authService.setFieldsUsuario(
                "email",
                "bdd." + java.util.UUID.randomUUID() + "@example.com"
        );
        authService.setFieldsUsuario("password", "password");
        authService.setFieldsUsuario("role", "admin");
        authService.setFieldsUsuario("company_cnpj", cnpj);
        authService.setFieldsUsuario("department", "Testing");

        // Cadastra o usuário
        authService.createUsuario("/api/auth/sign-up");

        Assert.assertEquals(
                "Não foi possível cadastrar o usuário temporário",
                201,
                authService.response.statusCode()
        );

        // Utiliza o token do usuário vinculado à empresa temporária
        String token = authService.getToken();

        Assert.assertNotNull(
                "O token JWT do usuário temporário não foi retornado",
                token
        );

        companyService.setToken(token);
    }

    @Quando("eu consultar essa empresa")
    public void euConsultarEssaEmpresa() {
        companyService.getCompany(COMPANY_ENDPOINT, companyId);
    }

    @Quando("eu consultar a empresa com ID inexistente")
    public void consultarEmpresaInexistente() {

        companyService.getCompany(
                COMPANY_ENDPOINT,
                999999L
        );
    }

    // =========================
    // ATUALIZAÇÃO
    // =========================

    @Quando("eu atualizar os dados dessa empresa")
    public void atualizarDadosDaEmpresa() {

        companyService.updateCompany(
                COMPANY_ENDPOINT,
                companyId,
                "Empresa BDD Atualizada",
                gerarCnpjUnico(),
                90.0
        );
    }

    @Quando("eu atualizar essa empresa utilizando um CNPJ já cadastrado")
    public void atualizarEmpresaComCnpjJaCadastrado() {

        String cnpjDuplicado = gerarCnpjUnico();

        // Cria uma segunda empresa para provocar o conflito.
        companyService.createCompany(
                COMPANY_ENDPOINT,
                "Empresa BDD Conflito",
                cnpjDuplicado,
                80.0
        );

        Assert.assertEquals(
                "Não foi possível criar a empresa necessária para o teste de conflito",
                201,
                companyService.response.statusCode()
        );

        // Tenta utilizar o CNPJ da segunda empresa na empresa original.
        companyService.updateCompany(
                COMPANY_ENDPOINT,
                companyId,
                "Empresa BDD Atualizada",
                cnpjDuplicado,
                90.0
        );
    }

    // =========================
    // EXCLUSÃO
    // =========================

    @Quando("eu excluir essa empresa")
    public void excluirEssaEmpresa() {

        companyService.deleteCompany(
                COMPANY_ENDPOINT,
                companyId
        );
    }

    // =========================
    // VALIDAÇÃO DE STATUS
    // =========================

    @Então("o status da resposta da empresa deve ser {int}")
    public void verificarStatusRespostaEmpresa(int statusCode) {

        Assert.assertNotNull(
                "Nenhuma resposta foi recebida da API de empresas",
                companyService.response
        );

        Assert.assertEquals(
                "O status HTTP retornado não é o esperado",
                statusCode,
                companyService.response.statusCode()
        );
    }

    // =========================
    // VALIDAÇÃO DOS CAMPOS
    // =========================

    @Então("a resposta da empresa deve possuir os campos obrigatórios")
    public void verificarCamposObrigatorios() {

        Assert.assertNotNull(
                "O ID da empresa não foi retornado",
                companyService.response.jsonPath().get("id")
        );

        Assert.assertNotNull(
                "O nome da empresa não foi retornado",
                companyService.response.jsonPath().get("name")
        );

        Assert.assertNotNull(
                "O CNPJ da empresa não foi retornado",
                companyService.response.jsonPath().get("cnpj")
        );

        Assert.assertNotNull(
                "A data de criação não foi retornada",
                companyService.response.jsonPath().get("created_at")
        );

        Assert.assertNotNull(
                "A data de atualização não foi retornada",
                companyService.response.jsonPath().get("updated_at")
        );
    }

    // =========================
    // RESPOSTA SEM CONTEÚDO
    // =========================

    @Então("a resposta da empresa não deve possuir conteúdo")
    public void verificarRespostaSemConteudo() {

        Assert.assertNotNull(
                "Nenhuma resposta foi recebida da API",
                companyService.response
        );

        Assert.assertEquals(
                "A resposta deveria estar vazia",
                "",
                companyService.response.getBody().asString()
        );
    }
}
