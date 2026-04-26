package com.pao.proiect.aibilet.model;

public class BiletCuLoc extends Bilet {
    private final String seatCode;

    public BiletCuLoc(int id, CodBilet codBilet, int evenimentId, int clientId, String seatCode, String tipBilet, double pret, StatusBilet status) {
        super(id, codBilet, evenimentId, clientId, tipBilet, pret, status);

        if (seatCode == null || seatCode.trim().length() == 0) {
            throw new IllegalArgumentException("Biletul cu loc trebuie sa aiba un cod de loc valid.");
        }

        this.seatCode = seatCode.trim();
    }

    public String getSeatCode() {
        return seatCode;
    }

    @Override
    public String toString() {
        return "Bilet cu loc\n" +
                bazaToString() + "\n" +
                "  Loc: " + seatCode;
    }
}
