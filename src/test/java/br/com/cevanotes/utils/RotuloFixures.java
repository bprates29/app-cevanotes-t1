package br.com.cevanotes.utils;

import br.com.cevanotes.model.Rotulo;

import java.time.LocalDate;

public class RotuloFixures {

    public static final int ID = 1;
    public static final String NOME_ROTULO = "IPA";
    public static final String ESTILO_ROTULO = "American IPA";
    public static final double TEOR_ALCOOLICO_ROTULO = 6.3;
    public static final String CERVEJARIA_ROTULO = "Cervejaria x";

    public static Rotulo buildRotulo() {
        return new Rotulo(ID,
                NOME_ROTULO,
                ESTILO_ROTULO,
                TEOR_ALCOOLICO_ROTULO,
                CERVEJARIA_ROTULO,
                LocalDate.now());
    }

}
