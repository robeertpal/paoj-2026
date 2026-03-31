package com.pao.laboratory06.exercise3;

public abstract class Persoana {
    protected String nume;
    protected String prenume;
    protected String telefon;

    public Persoana(String nume, String prenume, String telefon) {
        if (nume == null || nume.isBlank() || prenume == null || prenume.isBlank()) {
            throw new IllegalArgumentException("Numele si prenumele nu pot fi nule sau goale.");
        }
        this.nume = nume;
        this.prenume = prenume;
        this.telefon = telefon;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getTelefon() {
        return telefon;
    }

    @Override
    public String toString() {
        return nume + " " + prenume;
    }
}