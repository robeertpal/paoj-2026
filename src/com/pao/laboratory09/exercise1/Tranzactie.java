package com.pao.laboratory09.exercise1;

import java.io.Serializable;

public class Tranzactie implements Serializable {
    private static final long serialVersionUID = 1L;

    int id;
    double suma;
    String data;
    String contSursa;
    String contDestinatie;
    TipTranzactie tip;
    transient String note;

    public Tranzactie(int id, double suma, String data, String contSursa,
                      String contDestinatie, TipTranzactie tip) {
        this.id = id;
        this.suma = suma;
        this.data = data;
        this.contSursa = contSursa;
        this.contDestinatie = contDestinatie;
        this.tip = tip;
    }

    public String formatForOutput() {
        return String.format(
                java.util.Locale.US,
                "[%d] %s %s: %.2f RON | %s -> %s",
                id, data, tip, suma, contSursa, contDestinatie
        );
    }
}