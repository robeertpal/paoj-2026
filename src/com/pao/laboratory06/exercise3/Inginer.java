package com.pao.laboratory06.exercise3;

public class Inginer extends Angajat implements PlataOnline, Comparable<Inginer> {
    private String userValid;
    private String parolaValida;
    private double sold;
    private boolean autentificat;

    public Inginer(String nume, String prenume, String telefon, double salariu,
                   String userValid, String parolaValida, double sold) {
        super(nume, prenume, telefon, salariu);

        if (userValid == null || userValid.isBlank() || parolaValida == null || parolaValida.isBlank()) {
            throw new IllegalArgumentException("Credentialele initiale nu pot fi nule sau goale.");
        }
        if (sold < 0) {
            throw new IllegalArgumentException("Soldul nu poate fi negativ.");
        }

        this.userValid = userValid;
        this.parolaValida = parolaValida;
        this.sold = sold;
        this.autentificat = false;
    }

    @Override
    public void autentificare(String user, String parola) {
        if (user == null || user.isBlank() || parola == null || parola.isBlank()) {
            throw new IllegalArgumentException("User-ul si parola nu pot fi nule sau goale.");
        }

        this.autentificat = user.equals(userValid) && parola.equals(parolaValida);
        if (!autentificat) {
            System.out.println("Autentificare esuata pentru inginerul " + this + ".");
        } else {
            System.out.println("Autentificare reusita pentru inginerul " + this + ".");
        }
    }

    @Override
    public double consultareSold() {
        return sold;
    }

    @Override
    public boolean efectuarePlata(double suma) {
        if (suma <= 0) {
            throw new IllegalArgumentException("Suma trebuie sa fie pozitiva.");
        }
        if (!autentificat) {
            return false;
        }
        if (suma > sold) {
            return false;
        }

        sold -= suma;
        return true;
    }

    @Override
    public int compareTo(Inginer other) {
        return this.nume.compareTo(other.nume);
    }

    @Override
    public String toString() {
        return String.format("Inginer %s %s, salariu=%.2f, sold=%.2f",
                nume, prenume, salariu, sold);
    }
}