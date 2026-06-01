package com.pao.proiect.aibilet.service;

import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.model.Comanda;
import com.pao.proiect.aibilet.model.dto.ComandaClientView;
import com.pao.proiect.aibilet.repository.ComandaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ComandaService {
    private static final ComandaService INSTANCE = new ComandaService();
    private static final int CAPACITATE_MAXIMA = 1000;

    private final Comanda[] comenzi;
    private final ComandaRepository comandaRepository = ComandaRepository.getInstance();
    private int numarComenzi;
    private int nextId;

    private ComandaService() {
        this.comenzi = new Comanda[CAPACITATE_MAXIMA];
        this.numarComenzi = 0;
        this.nextId = 1;
    }

    public static ComandaService getInstance() {
        return INSTANCE;
    }

    public Comanda creeazaComanda(int clientId, int[] ticketIds, double total) {
        if (ticketIds == null) {
            throw new IllegalArgumentException("Lista de bilete nu poate fi null.");
        }

        verificaSpatiuDisponibil();

        int[] copieTicketIds = copiazaTicketIds(ticketIds);

        Comanda comanda = new Comanda(
                0,
                clientId,
                copieTicketIds,
                total,
                LocalDateTime.now().toString()
        );

        comandaRepository.create(comanda);
        adaugaSauInlocuiesteInCache(comanda);
        return comanda;
    }

    public Comanda[] cautaDupaClient(int clientId) {
        incarcaComenzileDinBaza();
        int count = 0;

        for (int i = 0; i < numarComenzi; i++) {
            if (comenzi[i].getClientId() == clientId) {
                count++;
            }
        }

        Comanda[] rezultat = new Comanda[count];
        int index = 0;

        for (int i = 0; i < numarComenzi; i++) {
            if (comenzi[i].getClientId() == clientId) {
                rezultat[index] = comenzi[i];
                index++;
            }
        }

        return rezultat;
    }

    public List<ComandaClientView> cautaComenziClientCuNumarBilete(int clientId) {
        return comandaRepository.findComenziClientCuNumarBilete(clientId);
    }

    public Comanda findById(int id) throws EntitateInexistentaException {
        Optional<Comanda> comanda = comandaRepository.findById(id);

        if (comanda.isPresent()) {
            adaugaSauInlocuiesteInCache(comanda.get());
            return comanda.get();
        }

        throw new EntitateInexistentaException("Comanda cu id=" + id + " nu exista.");
    }

    public Comanda[] listAll() {
        incarcaComenzileDinBaza();
        Comanda[] copie = new Comanda[numarComenzi];

        for (int i = 0; i < numarComenzi; i++) {
            copie[i] = comenzi[i];
        }

        return copie;
    }

    public Comanda actualizeaza(Comanda comandaActualizata) throws EntitateInexistentaException {
        if (comandaActualizata == null) {
            throw new IllegalArgumentException("Comanda actualizata nu poate fi null.");
        }

        if (!comandaRepository.findById(comandaActualizata.getId()).isPresent()) {
            throw new EntitateInexistentaException("Comanda cu id=" + comandaActualizata.getId() + " nu exista.");
        }

        comandaRepository.update(comandaActualizata);
        adaugaSauInlocuiesteInCache(comandaActualizata);
        return comandaActualizata;
    }

    public void delete(int id) throws EntitateInexistentaException {
        findById(id);
        comandaRepository.delete(id);

        stergeDinCache(id);
    }

    private void verificaSpatiuDisponibil() {
        if (numarComenzi >= CAPACITATE_MAXIMA) {
            throw new IllegalStateException("S-a atins capacitatea maxima de comenzi.");
        }
    }

    private void incarcaComenzileDinBaza() {
        List<Comanda> comenziDinBaza = comandaRepository.findAll();
        reseteazaCache(comenziDinBaza);
    }

    private void reseteazaCache(List<Comanda> comenziDinBaza) {
        for (int i = 0; i < comenzi.length; i++) {
            comenzi[i] = null;
        }

        numarComenzi = 0;
        nextId = 1;

        for (int i = 0; i < comenziDinBaza.size(); i++) {
            adaugaSauInlocuiesteInCache(comenziDinBaza.get(i));
        }
    }

    private void adaugaSauInlocuiesteInCache(Comanda comanda) {
        for (int i = 0; i < numarComenzi; i++) {
            if (comenzi[i] != null && comenzi[i].getId() == comanda.getId()) {
                comenzi[i] = comanda;
                actualizeazaNextId(comanda.getId());
                return;
            }
        }

        verificaSpatiuDisponibil();
        comenzi[numarComenzi] = comanda;
        numarComenzi++;
        actualizeazaNextId(comanda.getId());
    }

    private void stergeDinCache(int id) {
        int pozitie = -1;

        for (int i = 0; i < numarComenzi; i++) {
            if (comenzi[i] != null && comenzi[i].getId() == id) {
                pozitie = i;
                break;
            }
        }

        if (pozitie == -1) {
            return;
        }

        for (int i = pozitie; i < numarComenzi - 1; i++) {
            comenzi[i] = comenzi[i + 1];
        }

        comenzi[numarComenzi - 1] = null;
        numarComenzi--;
    }

    private void actualizeazaNextId(int id) {
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    private int[] copiazaTicketIds(int[] ticketIds) {
        int[] copie = new int[ticketIds.length];

        for (int i = 0; i < ticketIds.length; i++) {
            copie[i] = ticketIds[i];
        }

        return copie;
    }

}
