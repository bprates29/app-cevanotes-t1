package br.com.cevanotes.service;

import br.com.cevanotes.model.Rotulo;
import br.com.cevanotes.repository.RotuloRepository;
import io.javalin.http.NotFoundResponse;

import java.util.List;

public class RotuloService {
    private final RotuloRepository repository;

    public RotuloService (RotuloRepository repository) {
        this.repository = repository;
    }

    public List<Rotulo> listar() {
        return repository.findAll();
    }

    public Rotulo buscarPorId(int id) {
        return  repository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Rotulo Não encontrado!"));
    }
}
