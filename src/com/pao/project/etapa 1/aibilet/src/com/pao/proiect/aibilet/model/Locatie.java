package com.pao.proiect.aibilet.model;

public class Locatie {
    private int id;
    private String denumire;
    private String oras;
    private String adresa;
    private boolean suportaLocuri;

    public Locatie(int id, String denumire, String oras, String adresa, boolean suportaLocuri) {
        if (denumire == null) {
            throw new IllegalArgumentException("Denumirea nu poate fi null.");
        }
        if (oras == null) {
            throw new IllegalArgumentException("Orasul nu poate fi null.");
        }
        if (adresa == null) {
            throw new IllegalArgumentException("Adresa nu poate fi null.");
        }

        this.id = id;
        this.denumire = denumire;
        this.oras = oras;
        this.adresa = adresa;
        this.suportaLocuri = suportaLocuri;
    }

    public boolean permiteEvenimenteCuLocuri() {
        return suportaLocuri;
    }

    public boolean permiteEvenimenteFaraLocuri() {
        return !suportaLocuri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        if (denumire == null) {
            throw new IllegalArgumentException("Denumirea nu poate fi null.");
        }
        this.denumire = denumire;
    }

    public String getOras() {
        return oras;
    }

    public void setOras(String oras) {
        if (oras == null) {
            throw new IllegalArgumentException("Orasul nu poate fi null.");
        }
        this.oras = oras;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        if (adresa == null) {
            throw new IllegalArgumentException("Adresa nu poate fi null.");
        }
        this.adresa = adresa;
    }

    public boolean isSuportaLocuri() {
        return suportaLocuri;
    }

    public void setSuportaLocuri(boolean suportaLocuri) {
        this.suportaLocuri = suportaLocuri;
    }

    @Override
    public String toString() {
        return "Locatie\n" +
                "  ID: " + id + "\n" +
                "  Denumire: " + denumire + "\n" +
                "  Oras: " + oras + "\n" +
                "  Adresa: " + adresa + "\n" +
                "  Suporta locuri: " + suportaLocuri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Locatie)) {
            return false;
        }

        Locatie locatie = (Locatie) o;
        return id == locatie.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}