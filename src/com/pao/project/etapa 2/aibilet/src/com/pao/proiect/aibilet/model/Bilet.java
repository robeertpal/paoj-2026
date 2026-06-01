package com.pao.proiect.aibilet.model;

public abstract class Bilet {
    protected int id;
    private final CodBilet codBilet;
    private final int evenimentId;
    private final int clientId;
    private final String tipBilet;
    private double pret;
    private StatusBilet status;

    protected Bilet(int id, CodBilet codBilet, int evenimentId, int clientId, String tipBilet, double pret, StatusBilet status) {
        if (codBilet == null) {
            throw new IllegalArgumentException("Codul de bilet nu poate fi null.");
        }
        if (tipBilet == null) {
            throw new IllegalArgumentException("Tipul de bilet nu poate fi null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Statusul nu poate fi null.");
        }

        this.id = id;
        this.codBilet = codBilet;
        this.evenimentId = evenimentId;
        this.clientId = clientId;
        this.tipBilet = tipBilet;
        this.pret = pret;
        this.status = status;
    }

    public String getTipBilet() {
        return tipBilet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CodBilet getCodBilet() {
        return codBilet;
    }

    public int getEvenimentId() {
        return evenimentId;
    }

    public int getClientId() {
        return clientId;
    }

    public double getPret() {
        return pret;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }

    public StatusBilet getStatus() {
        return status;
    }

    public void setStatus(StatusBilet status) {
        if (status == null) {
            throw new IllegalArgumentException("Statusul nu poate fi null.");
        }
        this.status = status;
    }

    protected String bazaToString() {
        return "  ID: " + id + "\n" +
                "  Cod: " + codBilet + "\n" +
                "  Tip bilet: " + tipBilet + "\n" +
                "  Eveniment ID: " + evenimentId + "\n" +
                "  Client ID: " + clientId + "\n" +
                "  Pret: " + pret + "\n" +
                "  Status: " + status;
    }
}