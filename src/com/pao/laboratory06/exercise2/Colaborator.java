package com.pao.laboratory06.exercise2;

public abstract class Colaborator implements IOperatiiCitireScriere {
    protected String nume;
    protected String prenume;
    protected double venitBrutLunar;
    protected TipColaborator tip;

    public Colaborator(TipColaborator tip) {
        this.tip = tip;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public double getVenitBrutLunar() {
        return venitBrutLunar;
    }

    public TipColaborator getTip() {
        return tip;
    }

    public abstract double calculeazaVenitNetAnual();

    @Override
    public void afiseaza() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return String.format("%s: %s %s, venit net anual: %.2f lei",
                tipContract(), nume, prenume, calculeazaVenitNetAnual());
    }
}