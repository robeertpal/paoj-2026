package com.pao.proiect.aibilet.model.dto;

public class BiletClientView {
    private final int biletId;
    private final String codBilet;
    private final String statusBilet;
    private final double pret;
    private final String titluEveniment;
    private final String numeLocatie;
    private final String oras;
    private final String codLoc;
    private final Integer rand;
    private final Integer coloana;

    public BiletClientView(int biletId, String codBilet, String statusBilet, double pret, String titluEveniment,
                           String numeLocatie, String oras, String codLoc, Integer rand, Integer coloana) {
        this.biletId = biletId;
        this.codBilet = codBilet;
        this.statusBilet = statusBilet;
        this.pret = pret;
        this.titluEveniment = titluEveniment;
        this.numeLocatie = numeLocatie;
        this.oras = oras;
        this.codLoc = codLoc;
        this.rand = rand;
        this.coloana = coloana;
    }

    public int getBiletId() {
        return biletId;
    }

    public String getCodBilet() {
        return codBilet;
    }

    public String getStatusBilet() {
        return statusBilet;
    }

    public double getPret() {
        return pret;
    }

    public String getTitluEveniment() {
        return titluEveniment;
    }

    public String getNumeLocatie() {
        return numeLocatie;
    }

    public String getOras() {
        return oras;
    }

    public String getCodLoc() {
        return codLoc;
    }

    public Integer getRand() {
        return rand;
    }

    public Integer getColoana() {
        return coloana;
    }

    @Override
    public String toString() {
        String loc = codLoc == null ? "fara loc" : codLoc + " (rand=" + rand + ", coloana=" + coloana + ")";
        return "BiletClientView{" +
                "biletId=" + biletId +
                ", codBilet='" + codBilet + '\'' +
                ", statusBilet='" + statusBilet + '\'' +
                ", pret=" + pret +
                ", titluEveniment='" + titluEveniment + '\'' +
                ", numeLocatie='" + numeLocatie + '\'' +
                ", oras='" + oras + '\'' +
                ", loc='" + loc + '\'' +
                '}';
    }
}
