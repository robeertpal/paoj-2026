package com.pao.laboratory07.exercise2;

public final class ComandaStandard extends Comanda {

    public ComandaStandard(int id, String client, double valoare) {
        super(id, client, valoare);
    }

    @Override
    public void procesare() {
        afiseaza();
    }

    @Override
    public void afiseaza() {
        System.out.printf("STANDARD: %d %s, valoare: %.2f lei%n", id, client, valoare);
    }

    @Override
    public String tipComanda() {
        return "STANDARD";
    }

    @Override
    public String toString() {
        return String.format("STANDARD: %d %s, valoare: %.2f lei", id, client, valoare);
    }
}