package br.com.cevanotes.repository;

import br.com.cevanotes.model.Rotulo;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;

public class RotuloRepository {
    private final Jdbi jdbi;

    public RotuloRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<Rotulo> findAll() {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM rotulos")
                .mapToBean(Rotulo.class)
                .list());
    }

    public Optional<Rotulo> findById(int id) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM rotulos WHERE id = :id")
                        .bind("id", id)
                        .mapToBean(Rotulo.class)
                        .findOne());

    }
}
