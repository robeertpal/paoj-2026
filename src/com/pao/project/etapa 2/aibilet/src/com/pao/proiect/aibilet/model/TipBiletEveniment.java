package com.pao.proiect.aibilet.model;

public class TipBiletEveniment {
    private int id;
    private int evenimentId;
    private final String nume;
    private double pret;
    private int stocTotal;
    private int stocDisponibil;

    public TipBiletEveniment(String nume, double pret, int stocTotal, int stocDisponibil) {
        this(0, 0, nume, pret, stocTotal, stocDisponibil);
    }

    public TipBiletEveniment(int id, int evenimentId, String nume, double pret, int stocTotal, int stocDisponibil) {
        if (nume == null || nume.trim().length() == 0) {
            throw new IllegalArgumentException("Tipul de bilet este obligatoriu.");
        }

        this.id = id;
        this.evenimentId = evenimentId;
        this.nume = nume.trim();
        this.pret = pret;

        if (stocTotal < 0) {
            stocTotal = 0;
        }

        if (stocDisponibil < 0) {
            stocDisponibil = 0;
        }

        if (stocDisponibil > stocTotal) {
            stocDisponibil = stocTotal;
        }

        this.stocTotal = stocTotal;
        this.stocDisponibil = stocDisponibil;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvenimentId() {
        return evenimentId;
    }

    public void setEvenimentId(int evenimentId) {
        this.evenimentId = evenimentId;
    }

    public String getNume() {
        return nume;
    }

    public double getPret() {
        return pret;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }

    public int getStocTotal() {
        return stocTotal;
    }

    public void setStocTotal(int stocTotal) {
        if (stocTotal < 0) {
            stocTotal = 0;
        }

        this.stocTotal = stocTotal;

        if (this.stocDisponibil > this.stocTotal) {
            this.stocDisponibil = this.stocTotal;
        }
    }

    public int getStocDisponibil() {
        return stocDisponibil;
    }

    public void setStocDisponibil(int stocDisponibil) {
        if (stocDisponibil < 0) {
            stocDisponibil = 0;
        }

        if (stocDisponibil > stocTotal) {
            stocDisponibil = stocTotal;
        }

        this.stocDisponibil = stocDisponibil;
    }

    public void scadeStoc(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Numarul de bilete nu poate fi negativ.");
        }

        if (count > stocDisponibil) {
            throw new IllegalArgumentException("Nu exista suficiente bilete disponibile pentru tipul " + nume + ".");
        }

        stocDisponibil = stocDisponibil - count;
    }

    @Override
    public String toString() {
        return "TipBiletEveniment\n" +
                "  ID: " + id + "\n" +
                "  Eveniment ID: " + evenimentId + "\n" +
                "  Nume: " + nume + "\n" +
                "  Pret: " + pret + "\n" +
                "  Stoc total: " + stocTotal + "\n" +
                "  Stoc disponibil: " + stocDisponibil;
    }
}
