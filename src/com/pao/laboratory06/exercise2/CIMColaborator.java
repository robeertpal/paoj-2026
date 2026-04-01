package com.pao.laboratory06.exercise2;

import java.util.Scanner;

public class CIMColaborator extends PersoanaFizica {
    private boolean bonus;

    public CIMColaborator() {
        super(TipColaborator.CIM);
    }

    @Override
    public void citeste(Scanner in) {
        this.nume = in.next();
        this.prenume = in.next();
        this.venitBrutLunar = in.nextDouble();

        if (in.hasNext()) {
            String bonusText = in.next();
            this.bonus = bonusText.equalsIgnoreCase("DA");
        } else {
            this.bonus = false;
        }
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
    public double calculeazaVenitNetAnual() {
        double rezultat = venitBrutLunar * 12 * 0.55;
        if (bonus) {
            rezultat *= 1.10;
        }
        return rezultat;
    }
}