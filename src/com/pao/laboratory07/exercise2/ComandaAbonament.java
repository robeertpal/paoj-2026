package com.pao.laboratory07.exercise2;

public final class ComandaAbonament extends Comanda {

    private int nrLuni;

    public ComandaAbonament(int id, String client, double valoare, int nrLuni) {
        super(id, client, valoare);
        this.nrLuni = nrLuni;
    }

    @Override
    public void procesare() {
        afiseaza();
    }

    @Override
    public void afiseaza() {
        System.out.printf("ABONAMENT: %d %s, valoare: %.2f lei, luni: %d%n",
                id, client, valoare, nrLuni);
    }

    @Override
    public String tipComanda() {
        return "ABONAMENT";
    }

    @Override
    public boolean esteSpeciala() {
        return true;
    }

    @Override
    public String toString() {
        return String.format("ABONAMENT: %d %s, valoare: %.2f lei, luni: %d",
                id, client, valoare, nrLuni);
    }
}