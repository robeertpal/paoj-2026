package com.pao.proiect.aibilet.model;

public class LocEveniment {
    private int id;
    private int evenimentId;
    private final int rand;
    private final int coloana;
    private final String cod;
    private String tipBilet;
    private StatusLoc status;

    public LocEveniment(int rand, int coloana, String cod, String tipBilet, StatusLoc status) {
        this(0, 0, rand, coloana, cod, tipBilet, status);
    }

    public LocEveniment(int id, int evenimentId, int rand, int coloana, String cod, String tipBilet, StatusLoc status) {
        if (cod == null) {
            throw new IllegalArgumentException("Codul nu poate fi null.");
        }
        if (tipBilet == null) {
            throw new IllegalArgumentException("Tipul biletului nu poate fi null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Statusul nu poate fi null.");
        }

        this.id = id;
        this.evenimentId = evenimentId;
        this.rand = rand;
        this.coloana = coloana;
        this.cod = cod;
        this.tipBilet = tipBilet;
        this.status = status;
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

    public int getRand() {
        return rand;
    }

    public int getColoana() {
        return coloana;
    }

    public String getCod() {
        return cod;
    }

    public String getTipBilet() {
        return tipBilet;
    }

    public void setTipBilet(String tipBilet) {
        if (tipBilet == null) {
            throw new IllegalArgumentException("Tipul biletului nu poate fi null.");
        }

        this.tipBilet = tipBilet;
    }

    public StatusLoc getStatus() {
        return status;
    }

    public void setStatus(StatusLoc status) {
        if (status == null) {
            throw new IllegalArgumentException("Statusul nu poate fi null.");
        }

        this.status = status;
    }

    public boolean esteDisponibil() {
        return status == StatusLoc.LIBER;
    }

    @Override
    public String toString() {
        return "LocEveniment\n" +
                "  ID: " + id + "\n" +
                "  Eveniment ID: " + evenimentId + "\n" +
                "  Rand: " + rand + "\n" +
                "  Coloana: " + coloana + "\n" +
                "  Cod: " + cod + "\n" +
                "  Tip bilet: " + tipBilet + "\n" +
                "  Status: " + status;
    }
}
