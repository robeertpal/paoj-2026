package com.pao.laboratory06.exercise3;

public class Angajat extends Persoana {
    protected double salariu;

    public Angajat(String nume, String prenume, String telefon, double salariu) {
        super(nume, prenume, telefon);
        if (salariu < 0) {
            throw new IllegalArgumentException("Salariul nu poate fi negativ.");
        }
        this.salariu = salariu;
    }

    public double getSalariu() {
        return salariu;
    }
}