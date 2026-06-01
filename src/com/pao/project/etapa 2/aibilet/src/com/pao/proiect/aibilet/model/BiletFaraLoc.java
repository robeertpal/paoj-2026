package com.pao.proiect.aibilet.model;

public class BiletFaraLoc extends Bilet {

    public BiletFaraLoc(int id, CodBilet codBilet, int evenimentId, int clientId, String tipBilet, double pret, StatusBilet status) {
        super(id, codBilet, evenimentId, clientId, tipBilet, pret, status);
    }

    @Override
    public String toString() {
        return "Bilet fara loc\n" +
                bazaToString() + "\n" +
                "  Loc: -";
    }
}
