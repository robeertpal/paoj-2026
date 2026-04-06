package com.pao.laboratory07.exercise2;

public sealed abstract class Comanda implements ActiuneComanda
        permits ComandaStandard, Precomanda, ComandaAbonament {

    protected int id;
    protected String client;
    protected double valoare;

    public Comanda(int id, String client, double valoare) {
        this.id = id;
        this.client = client;
        this.valoare = valoare;
    }

    public int getId() {
        return id;
    }

    public String getClient() {
        return client;
    }

    public double getValoare() {
        return valoare;
    }

    public abstract void procesare();

    @Override
    public void proceseaza() {
        // pentru partea B doar "procesează", fără output obligatoriu
    }
}