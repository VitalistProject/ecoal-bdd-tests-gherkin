
package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;

import org.junit.Assert;

import services.AuthService;
import services.CompanyService;

import java.util.Map;

public class CompanySteps {

    private final AuthService authService = new AuthService();
    private final CompanyService companyService = new CompanyService();

    private static final String COMPANY_ENDPOINT = "/api/companies";

    private Long companyId = 1L;

    // =========================
    // AUTENTICAÇÃO
    // =========================

    @Dado("que eu esteja autenticado para gerenciar empresas")
    public void autenticarParaGerenciarEmpresas() {

        Map<String, String> credentials = Map.of(
                "email", "guilhermino@gmail.com",
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
                data.get("cnpj"),
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
                "98765432000199",
                80.0
        );
    }

    // =========================
    // CONSULTA
    // =========================

    @Dado("que exista uma empresa cadastrada")
    public void queExistaUmaEmpresaCadastrada() {

        companyService.getCompany(
                COMPANY_ENDPOINT,
                companyId
        );

        Assert.assertEquals(
                "A empresa de referência não foi encontrada ou não está acessível",
                200,
                companyService.response.statusCode()
        );
    }

    @Quando("eu consultar essa empresa")
    public void consultarEssaEmpresa() {

        companyService.getCompany(
                COMPANY_ENDPOINT,
                companyId
        );
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
                "98765432000199",
                90.0
        );
    }

    @Quando("eu atualizar essa empresa utilizando um CNPJ já cadastrado")
    public void atualizarEmpresaComCnpjJaCadastrado() {

        companyService.updateCompany(
                COMPANY_ENDPOINT,
                companyId,
                "Empresa BDD Atualizada",
                "12345678000195",
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
