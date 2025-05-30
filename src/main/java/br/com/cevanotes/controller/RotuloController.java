package br.com.cevanotes.controller;

import br.com.cevanotes.config.DbConfig;
import br.com.cevanotes.repository.RotuloRepository;
import br.com.cevanotes.service.RotuloService;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;

public class RotuloController {
    private final RotuloService service;

    public RotuloController (RotuloService service) {
        this.service = service;
    }

    public void registrarRotas(Javalin app) {
        app.get("/rotulos", ctx -> ctx.json(service.listar()));
    }

}
