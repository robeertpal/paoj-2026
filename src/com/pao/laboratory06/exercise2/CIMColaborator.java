package com.pao.laboratory06.exercise2;

public class CIMColaborator extends PersoanaFizica {
    private boolean bonus;

    public CIMColaborator(String nume, String prenume, double venitBrutLunar, boolean bonus) {
        super(nume, prenume, venitBrutLunar);
        this.bonus = bonus;
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double rezultat = venitBrutLunar * 12 * 0.55;
        if (bonus) {
            rezultat *= 1.10;
        }
        return rezultat;
    }

    @Override
    public String tipContract() {
        return "CIM";
    }

    @Override
    public boolean areBonus() {
        return bonus;
    }

    @Override
    public TipColaborator getTip() {
        return TipColaborator.CIM;
    }
}