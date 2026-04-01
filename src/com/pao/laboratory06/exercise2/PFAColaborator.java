package com.pao.laboratory06.exercise2;

import java.util.Scanner;

public class PFAColaborator extends PersoanaFizica {
    private static final double SALARIU_MINIM_BRUT_ANUAL = 4050.0 * 12.0;

    private double cheltuieliLunare;

    public PFAColaborator() {
        super(TipColaborator.PFA);
    }

    @Override
    public void citeste(Scanner in) {
        this.nume = in.next();
        this.prenume = in.next();
        this.venitBrutLunar = in.nextDouble();
        this.cheltuieliLunare = in.nextDouble();
    }

    @Override
    public String tipContract() {
        return "PFA";
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double venitNetInainteDeTaxe = (venitBrutLunar - cheltuieliLunare) * 12.0;

        double impozit = 0.10 * venitNetInainteDeTaxe;

        double cass;
        if (venitNetInainteDeTaxe < 6 * SALARIU_MINIM_BRUT_ANUAL) {
            cass = 0.10 * (6 * SALARIU_MINIM_BRUT_ANUAL);
        } else if (venitNetInainteDeTaxe <= 72 * SALARIU_MINIM_BRUT_ANUAL) {
            cass = 0.10 * venitNetInainteDeTaxe;
        } else {
            cass = 0.10 * (72 * SALARIU_MINIM_BRUT_ANUAL);
        }

        double cas;
        if (venitNetInainteDeTaxe < 12 * SALARIU_MINIM_BRUT_ANUAL) {
            cas = 0.0;
        } else if (venitNetInainteDeTaxe <= 24 * SALARIU_MINIM_BRUT_ANUAL) {
            cas = 0.25 * (12 * SALARIU_MINIM_BRUT_ANUAL);
        } else {
            cas = 0.25 * (24 * SALARIU_MINIM_BRUT_ANUAL);
        }

        return venitNetInainteDeTaxe - impozit - cass - cas;
    }
}