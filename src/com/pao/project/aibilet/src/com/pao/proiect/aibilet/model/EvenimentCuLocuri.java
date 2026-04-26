package com.pao.proiect.aibilet.model;

public class EvenimentCuLocuri extends Eveniment {
    private HartaLocuri hartaLocuri;

    public EvenimentCuLocuri(int id, String titlu, String descriere, CategorieEveniment categorie, String dataOraInceput, String dataOraFinal, StatusEveniment status, int locatieId, int organizatorId, String numeOrganizatieOrganizator, HartaLocuri hartaLocuri) {
        super(id, titlu, descriere, categorie, dataOraInceput, dataOraFinal, status, locatieId, organizatorId, numeOrganizatieOrganizator);

        if (hartaLocuri == null) {
            throw new IllegalArgumentException("Harta locurilor nu poate fi null.");
        }

        this.hartaLocuri = hartaLocuri;
    }

    @Override
    public boolean esteCuLocuri() {
        return true;
    }

    @Override
    public int getDisponibilitate() {
        int locuriLibere = hartaLocuri.numarLocuriDisponibile();

        if (numarTipuriBilete == 0) {
            return locuriLibere;
        }

        int totalDisponibilPeTipuri = 0;

        for (int i = 0; i < numarTipuriBilete; i++) {
            totalDisponibilPeTipuri = totalDisponibilPeTipuri + tipuriBilete[i].getStocDisponibil();
        }

        return totalDisponibilPeTipuri;
    }

    public HartaLocuri getHartaLocuri() {
        return hartaLocuri;
    }

    public void setHartaLocuri(HartaLocuri hartaLocuri) {
        if (hartaLocuri == null) {
            throw new IllegalArgumentException("Harta locurilor nu poate fi null.");
        }

        this.hartaLocuri = hartaLocuri;
    }


}