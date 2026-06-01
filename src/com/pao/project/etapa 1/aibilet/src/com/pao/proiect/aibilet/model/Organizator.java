package com.pao.proiect.aibilet.model;

public class Organizator extends Utilizator {
    private String[] numeOrganizatii;
    private int numarOrganizatii;

    public Organizator(int id, String username, String parola, String nume, String prenume, String email, String telefon, String numeOrganizatie) {
        this(id, username, parola, nume, prenume, email, telefon, new String[]{numeOrganizatie});
    }

    public Organizator(int id, String username, String parola, String nume, String prenume, String email, String telefon, String[] numeOrganizatii) {
        super(id, username, parola, nume, prenume, email, telefon);
        this.numeOrganizatii = new String[20];
        this.numarOrganizatii = 0;
        setNumeOrganizatii(numeOrganizatii);
    }

    @Override
    public RolUtilizator getRol() {
        return RolUtilizator.ORGANIZATOR;
    }

    public String getNumeOrganizatie() {
        if (numarOrganizatii == 0) {
            return "";
        }

        return numeOrganizatii[0];
    }

    public void setNumeOrganizatie(String numeOrganizatie) {
        if (numeOrganizatie == null || numeOrganizatie.trim().length() == 0) {
            throw new IllegalArgumentException("Numele organizatiei nu poate fi null sau gol.");
        }

        this.numarOrganizatii = 1;
        this.numeOrganizatii[0] = numeOrganizatie.trim();
    }

    public String[] getNumeOrganizatii() {
        String[] copie = new String[numarOrganizatii];

        for (int i = 0; i < numarOrganizatii; i++) {
            copie[i] = numeOrganizatii[i];
        }

        return copie;
    }

    public void setNumeOrganizatii(String[] numeOrganizatiiNoi) {
        if (numeOrganizatiiNoi == null || numeOrganizatiiNoi.length == 0) {
            throw new IllegalArgumentException("Organizatorul trebuie sa fie asignat la cel putin o organizatie.");
        }

        this.numarOrganizatii = 0;

        for (int i = 0; i < numeOrganizatiiNoi.length; i++) {
            adaugaOrganizatie(numeOrganizatiiNoi[i]);
        }

        if (numarOrganizatii == 0) {
            throw new IllegalArgumentException("Nu exista nicio organizatie valida.");
        }
    }

    public void adaugaOrganizatie(String numeOrganizatie) {
        if (numeOrganizatie == null || numeOrganizatie.trim().length() == 0) {
            throw new IllegalArgumentException("Numele organizatiei nu poate fi null sau gol.");
        }

        String organizatie = numeOrganizatie.trim();

        for (int i = 0; i < numarOrganizatii; i++) {
            if (numeOrganizatii[i].equalsIgnoreCase(organizatie)) {
                return;
            }
        }

        if (numarOrganizatii == numeOrganizatii.length) {
            throw new IllegalStateException("S-a atins numarul maxim de organizatii pentru organizator.");
        }

        numeOrganizatii[numarOrganizatii] = organizatie;
        numarOrganizatii++;
    }

    public String getOrganizatiiCaText() {
        String rezultat = "";

        for (int i = 0; i < numarOrganizatii; i++) {
            if (i > 0) {
                rezultat += ",";
            }
            rezultat += numeOrganizatii[i];
        }

        return rezultat;
    }

    public void setOrganizatiiDinText(String text) {
        if (text == null || text.trim().length() == 0) {
            throw new IllegalArgumentException("Lista organizatiilor nu poate fi goala.");
        }

        String[] parti = text.split(",");
        setNumeOrganizatii(parti);
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "  Organizatii: " + getOrganizatiiCaText();
    }
}