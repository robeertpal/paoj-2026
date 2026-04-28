package com.pao.laboratory09.exercise3;

public class Tranzactie {
    private final int id;
    private final double suma;
    private final String data;
    private final int atmId;

    public Tranzactie(int id, double suma, String data, int atmId) {
        this.id = id;
        this.suma = suma;
        this.data = data;
        this.atmId = atmId;
    }

    public int getId() {
        return id;
    }

    public double getSuma() {
        return suma;
    }

    public String getData() {
        return data;
    }

    public int getAtmId() {
        return atmId;
    }
}