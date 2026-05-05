package com.pao.laboratory10.exercise1;

public class Tranzactie {
    private int id;
    private double suma;
    private String data;
    private TipTranzactie tip;

    public Tranzactie(int id, double suma, String data, TipTranzactie tip) {
        this.id = id;
        this.suma = suma;
        this.data = data;
        this.tip = tip;
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

    public TipTranzactie getTip() {
        return tip;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s %s: %.2f RON", id, data, tip, suma);
    }
}