package br.com.cevanotes;

import br.com.cevanotes.config.DbConfig;
import br.com.cevanotes.controller.RotuloController;
import br.com.cevanotes.repository.RotuloRepository;
import br.com.cevanotes.service.RotuloService;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

public class Main {
    private static String TOKEN_SECRETO = "meu-token-infnet-123";

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7070);
        var rotuloService = inicializacaoDosObjetos();

        validacaoDeAcesso(app);
        app.get("/teste", ctx -> ctx.result("API funcionando!"));
        new RotuloController(rotuloService).registrarRotas(app);
    }

    @NotNull
    private static RotuloService inicializacaoDosObjetos() {
        var dbConfig = DbConfig.createJdbi();
        var rotuloRepository = new RotuloRepository(dbConfig);
        return new RotuloService(rotuloRepository);
    }

    private static void validacaoDeAcesso(Javalin app) {
        app.before(ctx -> {
            String rota = ctx.path();
            if (rota.startsWith("/teste")) {
                return;
            }

            String tokenRecebido = ctx.header("Authorization");

            if (tokenRecebido == null || !tokenRecebido.equals(TOKEN_SECRETO)) {
                throw new UnauthorizedResponse("Token inválido ou ausente!");
            }
        });
    }
}