package com.pao.laboratory07.exercise2;

public final class Precomanda extends Comanda {

    private String dataLivrare;

    public Precomanda(int id, String client, double valoare, String dataLivrare) {
        super(id, client, valoare);
        this.dataLivrare = dataLivrare;
    }

    @Override
    public void procesare() {
        afiseaza();
    }

    @Override
    public void afiseaza() {
        System.out.printf("PRECOMANDA: %d %s, valoare: %.2f lei, livrare: %s%n",
                id, client, valoare, dataLivrare);
    }

    @Override
    public String tipComanda() {
        return "PRECOMANDA";
    }

    @Override
    public boolean esteSpeciala() {
        return true;
    }

    @Override
    public String toString() {
        return String.format("PRECOMANDA: %d %s, valoare: %.2f lei, livrare: %s",
                id, client, valoare, dataLivrare);
    }
}