package com.pao.proiect.aibilet.service;

import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.model.Comanda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class ComandaService {
    private static final ComandaService INSTANCE = new ComandaService();
    private static final String NUME_FISIER = "orders.csv";
    private static final int CAPACITATE_MAXIMA = 1000;

    private final Comanda[] comenzi;
    private int numarComenzi;
    private int nextId;

    private ComandaService() {
        this.comenzi = new Comanda[CAPACITATE_MAXIMA];
        this.numarComenzi = 0;
        this.nextId = 1;
        load();
    }

    public static ComandaService getInstance() {
        return INSTANCE;
    }

    private int genereazaId() {
        int id = nextId;
        nextId++;
        return id;
    }

    public Comanda creeazaComanda(int clientId, int[] ticketIds, double total) {
        if (ticketIds == null) {
            throw new IllegalArgumentException("Lista de bilete nu poate fi null.");
        }

        verificaSpatiuDisponibil();

        int[] copieTicketIds = copiazaTicketIds(ticketIds);

        Comanda comanda = new Comanda(
                genereazaId(),
                clientId,
                copieTicketIds,
                total,
                LocalDateTime.now().toString()
        );

        comenzi[numarComenzi] = comanda;
        numarComenzi++;

        save();
        return comanda;
    }

    public Comanda[] cautaDupaClient(int clientId) {
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

    public Comanda findById(int id) throws EntitateInexistentaException {
        for (int i = 0; i < numarComenzi; i++) {
            if (comenzi[i].getId() == id) {
                return comenzi[i];
            }
        }

        throw new EntitateInexistentaException("Comanda cu id=" + id + " nu exista.");
    }

    public Comanda[] listAll() {
        Comanda[] copie = new Comanda[numarComenzi];

        for (int i = 0; i < numarComenzi; i++) {
            copie[i] = comenzi[i];
        }

        return copie;
    }

    public void deleteById(int id) throws EntitateInexistentaException {
        int pozitie = -1;

        for (int i = 0; i < numarComenzi; i++) {
            if (comenzi[i].getId() == id) {
                pozitie = i;
                break;
            }
        }

        if (pozitie == -1) {
            throw new EntitateInexistentaException("Comanda cu id=" + id + " nu exista.");
        }

        for (int i = pozitie; i < numarComenzi - 1; i++) {
            comenzi[i] = comenzi[i + 1];
        }

        comenzi[numarComenzi - 1] = null;
        numarComenzi--;

        save();
    }

    private void verificaSpatiuDisponibil() {
        if (numarComenzi >= CAPACITATE_MAXIMA) {
            throw new IllegalStateException("S-a atins capacitatea maxima de comenzi.");
        }
    }

    private int[] copiazaTicketIds(int[] ticketIds) {
        int[] copie = new int[ticketIds.length];

        for (int i = 0; i < ticketIds.length; i++) {
            copie[i] = ticketIds[i];
        }

        return copie;
    }

    private String transformaTicketIdsInText(int[] ticketIds) {
        if (ticketIds == null || ticketIds.length == 0) {
            return "";
        }

        String rezultat = "";

        for (int i = 0; i < ticketIds.length; i++) {
            rezultat = rezultat + ticketIds[i];

            if (i < ticketIds.length - 1) {
                rezultat = rezultat + ",";
            }
        }

        return rezultat;
    }

    private int[] transformaTextInTicketIds(String text) {
        if (text == null || text.trim().length() == 0) {
            return new int[0];
        }

        String[] bucati = text.split(",");
        int[] ids = new int[bucati.length];

        for (int i = 0; i < bucati.length; i++) {
            ids[i] = Integer.parseInt(bucati[i]);
        }

        return ids;
    }

    private void load() {
        File fisier = new File(NUME_FISIER);

        if (!fisier.exists()) {
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fisier));
            String linie;
            boolean primaLinie = true;

            while ((linie = reader.readLine()) != null) {
                if (linie.trim().length() == 0) {
                    continue;
                }

                if (primaLinie && linie.startsWith("id;")) {
                    primaLinie = false;
                    continue;
                }

                primaLinie = false;

                String[] p = linie.split(";", -1);

                if (p.length < 5) {
                    continue;
                }

                int id = Integer.parseInt(p[0]);
                int clientId = Integer.parseInt(p[1]);
                int[] ticketIds = transformaTextInTicketIds(p[2]);
                double total = Double.parseDouble(p[3]);
                String timestamp = p[4];

                Comanda comanda = new Comanda(id, clientId, ticketIds, total, timestamp);

                comenzi[numarComenzi] = comanda;
                numarComenzi++;

                if (id >= nextId) {
                    nextId = id + 1;
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Eroare la incarcarea comenzilor: " + e.getMessage());
        }
    }

    private void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(NUME_FISIER));

            writer.write("id;clientId;ticketIds;total;timestamp");
            writer.newLine();

            for (int i = 0; i < numarComenzi; i++) {
                Comanda comanda = comenzi[i];

                String linie =
                        comanda.getId() + ";" +
                                comanda.getClientId() + ";" +
                                transformaTicketIdsInText(comanda.getTicketIds()) + ";" +
                                comanda.getTotal() + ";" +
                                comanda.getTimestamp();

                writer.write(linie);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Eroare la salvarea comenzilor: " + e.getMessage());
        }
    }
}