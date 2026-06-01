package com.pao.proiect.aibilet.model;

public class EvenimentFaraLocuri extends Eveniment {
    private int capacitateTotala;
    private int locuriDisponibile;

    public EvenimentFaraLocuri(int id, String titlu, String descriere, CategorieEveniment categorie, String dataOraInceput, String dataOraFinal, StatusEveniment status, int locatieId, int organizatorId, String numeOrganizatieOrganizator, int capacitateTotala, int locuriDisponibile) {
        super(id, titlu, descriere, categorie, dataOraInceput, dataOraFinal, status, locatieId, organizatorId, numeOrganizatieOrganizator);

        if (capacitateTotala < 0) {
            capacitateTotala = 0;
        }
        if (locuriDisponibile < 0) {
            locuriDisponibile = 0;
        }

        this.capacitateTotala = capacitateTotala;
        this.locuriDisponibile = locuriDisponibile;
    }

    @Override
    public boolean esteCuLocuri() {
        return false;
    }

    @Override
    public int getDisponibilitate() {
        return locuriDisponibile;
    }

    public int getCapacitateTotala() {
        return capacitateTotala;
    }

    @Override
    public void seteazaTipuriBilete(TipBiletEveniment[] tipuri, int numarTipuri) {
        super.seteazaTipuriBilete(tipuri, numarTipuri);

        int total = 0;
        int disponibil = 0;

        for (int i = 0; i < numarTipuri; i++) {
            total = total + tipuri[i].getStocTotal();
            disponibil = disponibil + tipuri[i].getStocDisponibil();
        }

        this.capacitateTotala = total;
        this.locuriDisponibile = disponibil;
    }
}