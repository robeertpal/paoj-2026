package com.pao.laboratory06.exercise3;

public abstract class Persoana {
    protected String nume;
    protected String prenume;
    protected String telefon; // poate fi null sau gol

    public Persoana(String nume, String prenume, String telefon) {
        if (nume == null || nume.trim().isEmpty()) {
            throw new IllegalArgumentException("Numele nu poate fi null sau gol.");
        }
        if (prenume == null || prenume.trim().isEmpty()) {
            throw new IllegalArgumentException("Prenumele nu poate fi null sau gol.");
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

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    @Override
    public String toString() {
        return "Persoana{" +
                "nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", telefon='" + telefon + '\'' +
                '}';
    }
}