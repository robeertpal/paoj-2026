package com.pao.laboratory06.exercise3;

public class Inginer extends Angajat implements PlataOnline, Comparable<Inginer> {
    private double sold;
    private boolean autentificat;

    public Inginer(String nume, String prenume, String telefon, double salariu, double sold) {
        super(nume, prenume, telefon, salariu);

        if (sold < 0) {
            throw new IllegalArgumentException("Soldul nu poate fi negativ.");
        }

        this.sold = sold;
        this.autentificat = false;
    }

    @Override
    public void autentificare(String user, String parola) {
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("User-ul nu poate fi null sau gol.");
        }
        if (parola == null || parola.trim().isEmpty()) {
            throw new IllegalArgumentException("Parola nu poate fi null sau goala.");
        }

        autentificat = true;
        System.out.println("Inginerul " + nume + " s-a autentificat cu succes.");
    }

    @Override
    public double consultareSold() {
        return sold;
    }

    @Override
    public boolean efectuarePlata(double suma) {
        if (suma <= 0) {
            throw new IllegalArgumentException("Suma trebuie sa fie strict pozitiva.");
        }

        if (!autentificat) {
            System.out.println("Plata esuata: utilizatorul nu este autentificat.");
            return false;
        }

        if (suma > sold) {
            System.out.println("Plata esuata: fonduri insuficiente.");
            return false;
        }

        sold -= suma;
        return true;
    }

    @Override
    public int compareTo(Inginer altInginer) {
        if (altInginer == null) {
            throw new IllegalArgumentException("Obiectul comparat nu poate fi null.");
        }
        return this.nume.compareToIgnoreCase(altInginer.nume);
    }

    @Override
    public String toString() {
        return "Inginer{" +
                "nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", telefon='" + telefon + '\'' +
                ", salariu=" + salariu +
                ", sold=" + sold +
                ", autentificat=" + autentificat +
                '}';
    }
}