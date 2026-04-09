package com.pao.laboratory06.exercise3;

import java.util.ArrayList;
import java.util.List;

public class PersoanaJuridica extends Persoana implements PlataOnlineSMS {
    private double sold;
    private boolean autentificat;
    private List<String> smsTrimise;

    public PersoanaJuridica(String nume, String prenume, String telefon, double sold) {
        super(nume, prenume, telefon);

        if (sold < 0) {
            throw new IllegalArgumentException("Soldul nu poate fi negativ.");
        }

        this.sold = sold;
        this.autentificat = false;
        this.smsTrimise = new ArrayList<>();
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
        System.out.println("Persoana juridica " + nume + " s-a autentificat cu succes.");
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
            System.out.println("Plata esuata: entitatea nu este autentificata.");
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
    public boolean trimiteSMS(String mesaj) {
        if (mesaj == null || mesaj.trim().isEmpty()) {
            return false;
        }

        if (telefon == null || telefon.trim().isEmpty()) {
            return false;
        }

        smsTrimise.add(mesaj);
        return true;
    }

    public List<String> getSmsTrimise() {
        return smsTrimise;
    }

    @Override
    public String toString() {
        return "PersoanaJuridica{" +
                "nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", telefon='" + telefon + '\'' +
                ", sold=" + sold +
                ", autentificat=" + autentificat +
                ", smsTrimise=" + smsTrimise +
                '}';
    }
}