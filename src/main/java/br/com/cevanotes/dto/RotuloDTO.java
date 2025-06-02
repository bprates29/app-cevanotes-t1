package br.com.cevanotes.dto;

import br.com.cevanotes.model.Rotulo;

public class RotuloDTO {
    private String nome;
    private String estilo;
    private double teorAlcoolico;
    private String cervejaria;

    public RotuloDTO() {}

    public RotuloDTO(String nome, String estilo, double teorAlcoolico, String cervejaria) {
        this.nome = nome;
        this.estilo = estilo;
        this.teorAlcoolico = teorAlcoolico;
        this.cervejaria = cervejaria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }

    public double getTeorAlcoolico() {
        return teorAlcoolico;
    }

    public void setTeorAlcoolico(double teorAlcoolico) {
        this.teorAlcoolico = teorAlcoolico;
    }

    public String getCervejaria() {
        return cervejaria;
    }

    public void setCervejaria(String cervejaria) {
        this.cervejaria = cervejaria;
    }
}
