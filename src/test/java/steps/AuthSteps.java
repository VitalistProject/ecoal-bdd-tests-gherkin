package steps;

import com.networknt.schema.ValidationMessage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import model.ErrorFieldModel;
import model.ErrorMessageModel;
import org.junit.Assert;
import services.AuthService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuthSteps {
    AuthService authService = new AuthService();

    private Map<String, String> loginData;
    private String authToken;

    @Dado("que eu tenha os seguintes dados de cadastro:")
    public void queEuTenhaOsSeguintesDadosDeCadastro(List<Map<String, String>> rows) {
        for (Map<String, String> columns : rows) {
            authService.setFieldsUsuario(columns.get("campo"), columns.get("valor"));
        }
    }

    @Quando("eu enviar a requisição POST para o endpoint {string} de cadastro de usuários")
    public void euEnviarARequisicaoPostParaOEndpointDeCadastroDeUsuarios(String endPoint) {
        authService.createUsuario(endPoint);
    }

    @Então("o status code da resposta deve ser {int}")
    public void oStatusCodeDaRespostaDeveSer(int statusCode) {
        Assert.assertEquals(statusCode, authService.response.statusCode());
    }

    @E("o corpo de resposta de erro da api deve retornar a mensagem {string}")
    public void oCorpoDeRespostaDeErroDaApiDeveRetornarAMensagem(String message) {
        ErrorMessageModel errorMessageModel = authService.gson.fromJson(authService.response.jsonPath().prettify(), ErrorMessageModel.class);

        Assert.assertEquals(message, errorMessageModel.getFields().get("password"));
    }

    @E("o corpo de resposta de erro da api deve retornar a mensagem de erro do email {string}")
    public void oCorpoDeRespostaDeErroDaApiDeveRetornarAMensagemDeErroDoEmail(String message) {
        ErrorFieldModel errorFieldModel = authService.gson.fromJson(authService.response.jsonPath().prettify(), ErrorFieldModel.class);

        Assert.assertEquals(message, errorFieldModel.getError());
    }

    @E("o corpo de resposta de erro da api deve retornar a mensagem de erro do cnpj {string}")
    public void oCorpoDeRespostaDeErroDaApiDeveRetornarAMensagemDeErroDoCnpj(String message) {
        ErrorFieldModel errorFieldModel = authService.gson.fromJson(authService.response.jsonPath().prettify(), ErrorFieldModel.class);

        Assert.assertEquals(message, errorFieldModel.getError());
    }

    @E("que o arquivo de contrato esperado é o {string}")
    public void queOArquivoDeContratoEsperadoÉO(String contract) throws IOException {
        authService.setContract(contract);
    }

    @Então("a resposta da requisição deve estar em conformidade com o contrato selecionado")
    public void aRespostaDaRequisiçãoDeveEstarEmConformidadeComOContratoSelecionado() throws IOException {
        Set<ValidationMessage> validateResponse = authService.validateResponseAgainstSchema();
        Assert.assertTrue("O contrato está inválido. Erros encontrados: " + validateResponse, validateResponse.isEmpty());
    }

    // Recebe os dados de login da tabela Gherkin
    @Dado("que eu tenha os seguintes dados de login:")
    public void queEuTenhaOsSeguintesDadosDeLogin(DataTable dataTable) {
        loginData = dataTable.asMap(String.class, String.class);
    }

    // Envia a requisição POST de login
    @Quando("eu enviar a requisição POST para o endpoint {string} de autenticação de usuários")
    public void euEnviarARequisiçãoPOSTParaOEndpointDeAutenticaçãoDeUsuários(String endPoint) {
        authService.signIn(endPoint, loginData);
    }

    // Realiza login para obter um JWT válido
    @Dado("que eu esteja autenticado no sistema")
    public void queEuEstejaAutenticadoNoSistema() {

        Map<String, String> credentials = Map.of(
                "email", "guilhermino@gmail.com",
                "password", "password"
        );

        authService.signIn("/api/auth/sign-in", credentials);

        Assert.assertEquals(200, authService.response.statusCode());

        authToken = authService.getToken();

        Assert.assertNotNull("O token JWT não foi retornado", authToken);
    }

    // Envia a requisição GET de consulta do usuário
    @Quando("eu enviar a requisição GET para o endpoint {string} de consulta dos dados do usuário")
    public void euEnviarARequisiçãoGETParaOEndpointDeConsultaDosDadosDoUsuário(String endPoint) {
        authService.getMe(endPoint, authToken);
    }

    // Garante que a requisição será enviada sem autenticação
    @Dado("que eu não esteja autenticado no sistema")
    public void queEuNãoEstejaAutenticadoNoSistema() {
        authToken = null;
    }

    // Define um token inválido para a requisição
    @Dado("que eu esteja utilizando um token de autenticação inválido")
    public void queEuEstejaUtilizandoUmTokenDeAutenticaçãoInválido() {
        authToken = "token-invalido";
    }
}
