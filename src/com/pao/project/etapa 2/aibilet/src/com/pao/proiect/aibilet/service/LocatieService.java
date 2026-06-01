package com.pao.proiect.aibilet.service;

import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.model.Locatie;
import com.pao.proiect.aibilet.repository.LocatieRepository;

import java.util.List;
import java.util.Optional;

public class LocatieService {
    private static final LocatieService INSTANCE = new LocatieService();

    private final LocatieRepository locatieRepository;

    private LocatieService() {
        this.locatieRepository = LocatieRepository.getInstance();
    }

    public static LocatieService getInstance() {
        return INSTANCE;
    }

    public Locatie adaugaLocatie(String denumire, String oras, String adresa, boolean suportaLocuri) {
        if (denumire == null || denumire.trim().length() == 0) {
            throw new IllegalArgumentException("Denumirea locatiei este obligatorie.");
        }

        Locatie locatie = new Locatie(
            0,
            denumire,
            oras,
            adresa,
            suportaLocuri
        );

        return locatieRepository.create(locatie);
    }

    public Locatie[] cautaDupaNume(String query) {
        String q = "";
        if (query != null) {
            q = query.toLowerCase();
        }

        Locatie[] toate = listAll();
        int count = 0;

        for (int i = 0; i < toate.length; i++) {
            if (toate[i].getDenumire().toLowerCase().contains(q)) {
                count++;
            }
        }

        Locatie[] rezultat = new Locatie[count];
        int index = 0;

        for (int i = 0; i < toate.length; i++) {
            if (toate[i].getDenumire().toLowerCase().contains(q)) {
                rezultat[index] = toate[i];
                index++;
            }
        }

        return rezultat;
    }

    public Locatie findById(int id) throws EntitateInexistentaException {
        Optional<Locatie> locatie = locatieRepository.findById(id);

        if (locatie.isPresent()) {
            return locatie.get();
        }

        throw new EntitateInexistentaException("Locatia cu id=" + id + " nu exista.");
    }

    public void delete(int id) throws EntitateInexistentaException {
        findById(id);
        locatieRepository.delete(id);
    }

    public Locatie[] listAll() {
        List<Locatie> locatiiDinBaza = locatieRepository.findAll();
        Locatie[] copie = new Locatie[locatiiDinBaza.size()];

        for (int i = 0; i < locatiiDinBaza.size(); i++) {
            copie[i] = locatiiDinBaza.get(i);
        }

        return copie;
    }

}
