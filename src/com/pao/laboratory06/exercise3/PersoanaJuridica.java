package com.pao.laboratory06.exercise3;

import java.util.ArrayList;
import java.util.List;

public class PersoanaJuridica extends Persoana implements PlataOnlineSMS {
    private final List<String> smsTrimise;
    private String userValid;
    private String parolaValida;
    private double sold;
    private boolean autentificat;

    public PersoanaJuridica(String nume, String prenume, String telefon,
                            String userValid, String parolaValida, double sold) {
        super(nume, prenume, telefon);

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
        this.smsTrimise = new ArrayList<>();
    }

    @Override
    public void autentificare(String user, String parola) {
        if (user == null || user.isBlank() || parola == null || parola.isBlank()) {
            throw new IllegalArgumentException("User-ul si parola nu pot fi nule sau goale.");
        }

        this.autentificat = user.equals(userValid) && parola.equals(parolaValida);
        if (!autentificat) {
            System.out.println("Autentificare esuata pentru persoana juridica " + this + ".");
        } else {
            System.out.println("Autentificare reusita pentru persoana juridica " + this + ".");
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
    public boolean trimiteSMS(String mesaj) {
        if (mesaj == null || mesaj.isBlank()) {
            return false;
        }
        if (telefon == null || telefon.isBlank()) {
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
        return String.format("PersoanaJuridica %s %s, sold=%.2f", nume, prenume, sold);
    }
}