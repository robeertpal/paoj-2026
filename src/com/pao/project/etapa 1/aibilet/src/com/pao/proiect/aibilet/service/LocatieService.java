package com.pao.proiect.aibilet.service;

import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.model.Locatie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LocatieService {
    private static final LocatieService INSTANCE = new LocatieService();
    private static final String NUME_FISIER = "locations.csv";
    private static final int CAPACITATE_MAXIMA = 1000;

    private final Locatie[] locatii;
    private int numarLocatii;
    private int nextId;

    private LocatieService() {
        this.locatii = new Locatie[CAPACITATE_MAXIMA];
        this.numarLocatii = 0;
        this.nextId = 1;
        load();
    }

    public static LocatieService getInstance() {
        return INSTANCE;
    }

    private int genereazaId() {
        int id = nextId;
        nextId++;
        return id;
    }

    public Locatie adaugaLocatie(String denumire, String oras, String adresa, boolean suportaLocuri) {
        if (denumire == null || denumire.trim().length() == 0) {
            throw new IllegalArgumentException("Denumirea locatiei este obligatorie.");
        }

        verificaSpatiuDisponibil();

        Locatie locatie = new Locatie(
            genereazaId(),
            denumire,
            oras,
            adresa,
            suportaLocuri
        );

        locatii[numarLocatii] = locatie;
        numarLocatii++;

        save();
        return locatie;
    }

    public Locatie[] cautaDupaNume(String query) {
        String q = "";
        if (query != null) {
            q = query.toLowerCase();
        }

        int count = 0;

        for (int i = 0; i < numarLocatii; i++) {
            if (locatii[i].getDenumire().toLowerCase().contains(q)) {
                count++;
            }
        }

        Locatie[] rezultat = new Locatie[count];
        int index = 0;

        for (int i = 0; i < numarLocatii; i++) {
            if (locatii[i].getDenumire().toLowerCase().contains(q)) {
                rezultat[index] = locatii[i];
                index++;
            }
        }

        return rezultat;
    }

    public Locatie findById(int id) throws EntitateInexistentaException {
        for (int i = 0; i < numarLocatii; i++) {
            if (locatii[i].getId() == id) {
                return locatii[i];
            }
        }

        throw new EntitateInexistentaException("Locatia cu id=" + id + " nu exista.");
    }

    public void deleteById(int id) throws EntitateInexistentaException {
        int pozitie = -1;

        for (int i = 0; i < numarLocatii; i++) {
            if (locatii[i].getId() == id) {
                pozitie = i;
                break;
            }
        }

        if (pozitie == -1) {
            throw new EntitateInexistentaException("Locatia cu id=" + id + " nu exista.");
        }

        for (int i = pozitie; i < numarLocatii - 1; i++) {
            locatii[i] = locatii[i + 1];
        }

        locatii[numarLocatii - 1] = null;
        numarLocatii--;

        save();
    }

    public Locatie[] listAll() {
        Locatie[] copie = new Locatie[numarLocatii];

        for (int i = 0; i < numarLocatii; i++) {
            copie[i] = locatii[i];
        }

        return copie;
    }

    private void verificaSpatiuDisponibil() {
        if (numarLocatii >= CAPACITATE_MAXIMA) {
            throw new IllegalStateException("S-a atins capacitatea maxima de locatii.");
        }
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
                boolean suportaLocuri = Boolean.parseBoolean(p[4]);

                Locatie locatie = new Locatie(
                    id,
                    p[1],
                    p[2],
                    p[3],
                    suportaLocuri
                );

                locatii[numarLocatii] = locatie;
                numarLocatii++;

                if (id >= nextId) {
                    nextId = id + 1;
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Eroare la incarcarea locatiilor: " + e.getMessage());
        }
    }

    private void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(NUME_FISIER));

            writer.write("id;denumire;oras;adresa;suportaLocuri");
            writer.newLine();

            for (int i = 0; i < numarLocatii; i++) {
                Locatie locatie = locatii[i];

                String linie =
                        locatie.getId() + ";" +
                                locatie.getDenumire() + ";" +
                                locatie.getOras() + ";" +
                                locatie.getAdresa() + ";" +
                                locatie.isSuportaLocuri();

                writer.write(linie);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Eroare la salvarea locatiilor: " + e.getMessage());
        }
    }
}