package br.com.cevanotes.controller;

import br.com.cevanotes.config.DbConfig;
import br.com.cevanotes.dto.RotuloDTO;
import br.com.cevanotes.model.Rotulo;
import br.com.cevanotes.repository.RotuloRepository;
import br.com.cevanotes.service.RotuloService;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

public class RotuloController {
    public static final String ROTULOS_PATH = "/rotulos";
    public static final String ROTULOS_BY_ID_PATH = ROTULOS_PATH + "/{id}";
    private final RotuloService service;

    public RotuloController (RotuloService service) {
        this.service = service;
    }

    public void registrarRotas(Javalin app) {
        app.get(ROTULOS_PATH, ctx -> ctx.json(service.listar()));
        app.get(ROTULOS_BY_ID_PATH, ctx -> {
            var id = parseIdParam(ctx);
            ctx.json(service.buscarPorId(id));
        });
        app.post(ROTULOS_PATH, ctx -> {
            RotuloDTO dto = ctx.bodyValidator(RotuloDTO.class)
                    .check(r -> r.getNome() != null && !r.getNome().isBlank(), "Nome Obrigatório!")
                    .check(r -> r.getEstilo() != null && !r.getEstilo().isBlank(), "Estilo é obrigatório!")
                    .check(r -> r.getTeorAlcoolico() >= 0, "Teor alcoolico deve ser positivo")
                    .get();

            Rotulo novo = service.salvar(dto);
            ctx.status(201).json(novo);
        });
        app.put(ROTULOS_BY_ID_PATH, ctx -> {
            var id = parseIdParam(ctx);
            var r = ctx.bodyAsClass(RotuloDTO.class);
            var update = service.atualizar(id, r);
            ctx.status(200).json(update);
        });
        app.delete(ROTULOS_BY_ID_PATH, ctx -> {
            int id = parseIdParam(ctx);
            service.deletar(id);
            ctx.status(204);
        });
    }

    private int parseIdParam(Context ctx) {
        try {
            return Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("ID inválido. Use um número inteiro!");
        }
    }

}
