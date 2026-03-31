package com.pao.laboratory06.exercise2;

public class PFAColaborator extends PersoanaFizica {
    private static final double SALARIU_MINIM_ANUAL = 4050.0 * 12.0;

    private double cheltuieliLunare;

    public PFAColaborator(String nume, String prenume, double venitBrutLunar, double cheltuieliLunare) {
        super(nume, prenume, venitBrutLunar);
        this.cheltuieliLunare = cheltuieliLunare;
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double venitNet = (venitBrutLunar - cheltuieliLunare) * 12.0;
        double impozit = 0.10 * venitNet;

        double cass;
        if (venitNet < 6 * SALARIU_MINIM_ANUAL) {
            cass = 0.10 * (6 * SALARIU_MINIM_ANUAL);
        } else if (venitNet <= 72 * SALARIU_MINIM_ANUAL) {
            cass = 0.10 * venitNet;
        } else {
            cass = 0.10 * (72 * SALARIU_MINIM_ANUAL);
        }

        double cas;
        if (venitNet < 12 * SALARIU_MINIM_ANUAL) {
            cas = 0.0;
        } else if (venitNet <= 24 * SALARIU_MINIM_ANUAL) {
            cas = 0.25 * (12 * SALARIU_MINIM_ANUAL);
        } else {
            cas = 0.25 * (24 * SALARIU_MINIM_ANUAL);
        }

        return venitNet - impozit - cass - cas;
    }

    @Override
    public String tipContract() {
        return "PFA";
    }

    @Override
    public TipColaborator getTip() {
        return TipColaborator.PFA;
    }
}