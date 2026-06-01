package com.pao.proiect.aibilet.service;

import com.pao.proiect.aibilet.exception.AutentificareEsuataException;
import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.exception.LocIndisponibilException;
import com.pao.proiect.aibilet.exception.OperatieNepermisaException;
import com.pao.proiect.aibilet.model.AgentCheckIn;
import com.pao.proiect.aibilet.model.Bilet;
import com.pao.proiect.aibilet.model.Comanda;
import com.pao.proiect.aibilet.model.Eveniment;
import com.pao.proiect.aibilet.model.EvenimentCuLocuri;
import com.pao.proiect.aibilet.model.EvenimentFaraLocuri;
import com.pao.proiect.aibilet.model.LocEveniment;
import com.pao.proiect.aibilet.model.StatusBilet;
import com.pao.proiect.aibilet.model.TipBiletEveniment;
import com.pao.proiect.aibilet.model.Utilizator;
import com.pao.proiect.aibilet.model.dto.BiletClientView;
import com.pao.proiect.aibilet.model.dto.ComandaClientView;

import java.util.List;

public class TicketingService {
    private static final TicketingService INSTANCE = new TicketingService();

    private final UtilizatorService utilizatorService = UtilizatorService.getInstance();
    private final EvenimentService evenimentService = EvenimentService.getInstance();
    private final TicketService ticketService = TicketService.getInstance();
    private final ComandaService comandaService = ComandaService.getInstance();

    private TicketingService() {
    }

    public static TicketingService getInstance() {
        return INSTANCE;
    }

    public Utilizator login(String username, String parola) throws AutentificareEsuataException {
        return utilizatorService.login(username, parola);
    }

    public Bilet cumparaBiletCuLoc(int clientId, int evenimentId, String codLoc) throws EntitateInexistentaException, LocIndisponibilException, OperatieNepermisaException {

        Eveniment eveniment = evenimentService.findById(evenimentId);

        if (!(eveniment instanceof EvenimentCuLocuri)) {
            throw new OperatieNepermisaException("Evenimentul selectat nu este cu locuri.");
        }

        EvenimentCuLocuri evenimentCuLocuri = (EvenimentCuLocuri) eveniment;
        LocEveniment loc = evenimentCuLocuri.getHartaLocuri().cautaLocDupaCod(codLoc);

        if (!loc.esteDisponibil()) {
            throw new LocIndisponibilException("Locul " + loc.getCod() + " nu este disponibil.");
        }

        TipBiletEveniment tip = gasesteTipBiletPentruEvenimentCuLocuri(evenimentCuLocuri, loc.getTipBilet());
        double pret = tip.getPret();

        Bilet bilet = ticketService.emiteBilet(
                eveniment.getId(),
                eveniment.getTitlu(),
                clientId,
                loc.getCod(),
                loc.getTipBilet(),
                pret,
                StatusBilet.PLATIT
        );

        int[] ticketIds = new int[1];
        ticketIds[0] = bilet.getId();

        comandaService.creeazaComanda(clientId, ticketIds, bilet.getPret());

        return bilet;
    }

    public Bilet[] cumparaBileteFaraLoc(int clientId, int evenimentId, String tipBilet, int numarBilete) throws EntitateInexistentaException, OperatieNepermisaException {
        String[] tipuri = new String[1];
        int[] cantitati = new int[1];

        tipuri[0] = tipBilet;
        cantitati[0] = numarBilete;

        return cumparaBileteFaraLocCuTipuri(clientId, evenimentId, tipuri, cantitati);
    }

    public Bilet[] cumparaBileteFaraLocCuTipuri(int clientId, int evenimentId, String[] tipuriBilete, int[] cantitati) throws EntitateInexistentaException, OperatieNepermisaException {
        if (tipuriBilete == null || cantitati == null) {
            throw new OperatieNepermisaException("Datele pentru cumparare nu pot fi nule.");
        }

        if (tipuriBilete.length == 0 || cantitati.length == 0 || tipuriBilete.length != cantitati.length) {
            throw new OperatieNepermisaException("Tipurile de bilete si cantitatile sunt invalide.");
        }

        Eveniment eveniment = evenimentService.findById(evenimentId);

        if (!(eveniment instanceof EvenimentFaraLocuri)) {
            throw new OperatieNepermisaException("Evenimentul selectat necesita alegerea unui loc.");
        }

        EvenimentFaraLocuri evenimentFaraLocuri = (EvenimentFaraLocuri) eveniment;

        int totalBilete = 0;

        for (int i = 0; i < cantitati.length; i++) {
            if (cantitati[i] <= 0) {
                throw new OperatieNepermisaException("Numarul de bilete trebuie sa fie pozitiv.");
            }

            TipBiletEveniment tip = gasesteTipBiletPentruEvenimentFaraLocuri(evenimentFaraLocuri, tipuriBilete[i]);

            if (tip.getStocDisponibil() < cantitati[i]) {
                throw new OperatieNepermisaException("Nu exista suficiente bilete disponibile pentru tipul " + tip.getNume() + ".");
            }

            totalBilete = totalBilete + cantitati[i];
        }

        Bilet[] bilete = new Bilet[totalBilete];
        int[] ticketIds = new int[totalBilete];

        int indexBilet = 0;
        double totalComanda = 0;

        for (int i = 0; i < tipuriBilete.length; i++) {
            TipBiletEveniment tip = gasesteTipBiletPentruEvenimentFaraLocuri(evenimentFaraLocuri, tipuriBilete[i]);

            for (int j = 0; j < cantitati[i]; j++) {
                Bilet bilet = ticketService.emiteBilet(
                        eveniment.getId(),
                        eveniment.getTitlu(),
                        clientId,
                        null,
                        tip.getNume(),
                        tip.getPret(),
                        StatusBilet.PLATIT
                );

                bilete[indexBilet] = bilet;
                ticketIds[indexBilet] = bilet.getId();
                totalComanda = totalComanda + bilet.getPret();
                indexBilet++;
            }
        }

        comandaService.creeazaComanda(clientId, ticketIds, totalComanda);

        return bilete;
    }

    public Bilet[] getBileteClient(int clientId) {
        return ticketService.cautaDupaClient(clientId);
    }

    public List<BiletClientView> getBileteClientCuEvenimentSiLocatie(int clientId) {
        return ticketService.cautaBileteClientCuEvenimentSiLocatie(clientId);
    }

    public Comanda[] getComenziClient(int clientId) {
        return comandaService.cautaDupaClient(clientId);
    }

    public List<ComandaClientView> getComenziClientCuNumarBilete(int clientId) {
        return comandaService.cautaComenziClientCuNumarBilete(clientId);
    }

    public Bilet valideazaBiletCheckIn(String codBilet, AgentCheckIn agent) throws EntitateInexistentaException {
        return ticketService.valideazaBiletCheckIn(codBilet, agent);
    }

    private TipBiletEveniment gasesteTipBiletPentruEvenimentCuLocuri(EvenimentCuLocuri eveniment, String numeTip) throws OperatieNepermisaException {
        TipBiletEveniment[] tipuri = eveniment.getTipuriBilete();

        for (int i = 0; i < tipuri.length; i++) {
            if (tipuri[i].getNume().equalsIgnoreCase(numeTip)) {
                return tipuri[i];
            }
        }

        throw new OperatieNepermisaException("Tipul de bilet " + numeTip + " nu exista pentru eveniment.");
    }

    private TipBiletEveniment gasesteTipBiletPentruEvenimentFaraLocuri(EvenimentFaraLocuri eveniment, String numeTip) throws OperatieNepermisaException {
        TipBiletEveniment[] tipuri = eveniment.getTipuriBilete();

        for (int i = 0; i < tipuri.length; i++) {
            if (tipuri[i].getNume().equalsIgnoreCase(numeTip)) {
                return tipuri[i];
            }
        }

        throw new OperatieNepermisaException("Tipul de bilet " + numeTip + " nu exista pentru eveniment.");
    }
}
