package steps;

import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import services.AnalyticsService;

import org.junit.Assert;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.networknt.schema.ValidationMessage;
import services.AuthService;

public class AnalyticsSteps {
    private final AuthService authService = new AuthService();
    private final AnalyticsService analyticsService = new AnalyticsService();

    @io.cucumber.java.pt.Dado("que eu esteja autenticado para consultar os dados analíticos")
    public void autenticarParaConsultarDadosAnaliticos() {
        Map<String, String> credentials = Map.of(
                "email", "bob@example.com",
                "password", "password"
        );

        authService.signIn("/api/auth/sign-in", credentials);

        Assert.assertEquals(200, authService.response.statusCode());

        String token = authService.getToken();

        Assert.assertNotNull("O token JWT não foi retornado", token);

        analyticsService.setToken(token);
    }

    @Quando("eu consultar o endpoint {string} de progresso das dimensões")
    public void consultarProgressoDasDimensoes(String endPoint) {
        analyticsService.getAnalytics(endPoint);
    }

    @Quando("eu consultar o endpoint {string} de progresso das dimensões com período {string}")
    public void consultarProgressoDasDimensoesComPeriodo(
            String endPoint, String period) {

        analyticsService.getAnalytics(endPoint, period);
    }

    @Quando("eu consultar o endpoint {string} de pontuação geral")
    public void consultarPontuacaoGeral(String endPoint) {
        analyticsService.getAnalytics(endPoint);
    }

    @Quando("eu consultar o endpoint {string} de dados do dashboard")
    public void consultarDadosDashboard(String endPoint) {
        analyticsService.getAnalytics(endPoint);
    }

    @Então("o status da resposta deve ser {int}")
    public void verificarStatusResposta(int statusCode) {
        Assert.assertEquals(
                "O status HTTP retornado não é o esperado",
                statusCode,
                analyticsService.response.statusCode()
        );
    }

    @Então("a resposta deve atender ao contrato {string}")
    public void validarContrato(String contract) throws IOException {
        analyticsService.setContract(contract);

        Set<ValidationMessage> errors =
                analyticsService.validateResponseAgainstSchema();

        Assert.assertTrue(
                "A resposta não atende ao contrato JSON Schema: " + errors,
                errors.isEmpty()
        );
    }
}