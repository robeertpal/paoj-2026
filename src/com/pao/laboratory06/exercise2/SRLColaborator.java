package com.pao.laboratory06.exercise2;

public class SRLColaborator extends PersoanaJuridica {
    private double cheltuieliLunare;

    public SRLColaborator(String nume, String prenume, double venitBrutLunar, double cheltuieliLunare) {
        super(nume, prenume, venitBrutLunar);
        this.cheltuieliLunare = cheltuieliLunare;
    }

    @Override
    public double calculeazaVenitNetAnual() {
        return (venitBrutLunar - cheltuieliLunare) * 12.0 * 0.84;
    }

    @Override
    public String tipContract() {
        return "SRL";
    }

    @Override
    public TipColaborator getTip() {
        return TipColaborator.SRL;
    }
}