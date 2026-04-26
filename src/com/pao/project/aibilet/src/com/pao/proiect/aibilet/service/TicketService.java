package com.pao.proiect.aibilet.service;

import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.model.AgentCheckIn;
import com.pao.proiect.aibilet.model.Bilet;
import com.pao.proiect.aibilet.model.BiletCuLoc;
import com.pao.proiect.aibilet.model.BiletFaraLoc;
import com.pao.proiect.aibilet.model.CodBilet;
import com.pao.proiect.aibilet.model.StatusBilet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TicketService {
    private static final TicketService INSTANCE = new TicketService();
    private static final String NUME_FISIER = "tickets.csv";
    private static final int CAPACITATE_MAXIMA = 5000;

    private final Bilet[] bilete;
    private final Set<String> coduriBilete;
    private int numarBilete;
    private int nextId;

    private TicketService() {
        this.bilete = new Bilet[CAPACITATE_MAXIMA];
        this.coduriBilete = new HashSet<String>();
        this.numarBilete = 0;
        this.nextId = 1;
        load();
    }

    public static TicketService getInstance() {
        return INSTANCE;
    }

    private int genereazaId() {
        int id = nextId;
        nextId++;
        return id;
    }

    private String valoareCodBilet(Bilet bilet) {
        if (bilet == null || bilet.getCodBilet() == null) {
            return "";
        }

        return bilet.getCodBilet().getValoare();
    }

    public Bilet emiteBilet(int evenimentId, String titluEveniment, int clientId, String seatCode, String tipBilet, double pret, StatusBilet status) {
        verificaSpatiuDisponibil();

        CodBilet codBilet = genereazaCodBilet(titluEveniment);

        if (coduriBilete.contains(codBilet.getValoare())) {
            throw new IllegalStateException("Codul de bilet exista deja.");
        }

        Bilet bilet;

        if (seatCode == null || seatCode.trim().length() == 0) {
            bilet = new BiletFaraLoc(
                    genereazaId(),
                    codBilet,
                    evenimentId,
                    clientId,
                    tipBilet,
                    pret,
                    status);
        } else {
            bilet = new BiletCuLoc(
                    genereazaId(),
                    codBilet,
                    evenimentId,
                    clientId,
                    seatCode,
                    tipBilet,
                    pret,
                    status);
        }

        bilete[numarBilete] = bilet;
        numarBilete++;
        coduriBilete.add(codBilet.getValoare());

        save();
        return bilet;
    }

    public Bilet[] cautaDupaClient(int clientId) {
        int count = 0;

        for (int i = 0; i < numarBilete; i++) {
            if (bilete[i].getClientId() == clientId) {
                count++;
            }
        }

        Bilet[] rezultat = new Bilet[count];
        int index = 0;

        for (int i = 0; i < numarBilete; i++) {
            if (bilete[i].getClientId() == clientId) {
                rezultat[index] = bilete[i];
                index++;
            }
        }

        return rezultat;
    }

    public Bilet findById(int id) throws EntitateInexistentaException {
        for (int i = 0; i < numarBilete; i++) {
            if (bilete[i].getId() == id) {
                return bilete[i];
            }
        }

        throw new EntitateInexistentaException("Biletul cu id=" + id + " nu exista.");
    }

    public Bilet cautaDupaCod(String codBilet) throws EntitateInexistentaException {
        if (codBilet == null || codBilet.trim().equals("")) {
            throw new EntitateInexistentaException("Codul biletului nu poate fi gol.");
        }

        String codCautat = codBilet.trim();

        for (int i = 0; i < numarBilete; i++) {
            if (bilete[i] != null &&
                    bilete[i].getCodBilet() != null &&
                    bilete[i].getCodBilet().getValoare().equalsIgnoreCase(codCautat)) {
                return bilete[i];
            }
        }

        throw new EntitateInexistentaException("Nu exista bilet cu codul " + codBilet + ".");
    }

    public Bilet valideazaBiletCheckIn(String codBilet, AgentCheckIn agent) throws EntitateInexistentaException {
        if (agent == null) {
            throw new IllegalArgumentException("Agentul de check-in nu poate fi null.");
        }

        Bilet bilet = cautaDupaCod(codBilet);

        if (!agent.esteAsignatLaEveniment(bilet.getEvenimentId())) {
            throw new IllegalArgumentException("Agentul nu este asignat la evenimentul acestui bilet.");
        }

        if (bilet.getStatus() == StatusBilet.FOLOSIT) {
            throw new IllegalArgumentException("Biletul a fost deja folosit.");
        }

        if (bilet.getStatus() != StatusBilet.PLATIT) {
            throw new IllegalArgumentException("Biletul nu este platit si nu poate fi validat.");
        }

        bilet.setStatus(StatusBilet.FOLOSIT);
        save();

        return bilet;
    }

    public Bilet[] listAll() {
        Bilet[] copie = new Bilet[numarBilete];

        for (int i = 0; i < numarBilete; i++) {
            copie[i] = bilete[i];
        }

        return copie;
    }

    public void deleteById(int id) throws EntitateInexistentaException {
        int pozitie = -1;

        for (int i = 0; i < numarBilete; i++) {
            if (bilete[i].getId() == id) {
                pozitie = i;
                break;
            }
        }

        if (pozitie == -1) {
            throw new EntitateInexistentaException("Biletul cu id=" + id + " nu exista.");
        }

        coduriBilete.remove(valoareCodBilet(bilete[pozitie]));

        for (int i = pozitie; i < numarBilete - 1; i++) {
            bilete[i] = bilete[i + 1];
        }

        bilete[numarBilete - 1] = null;
        numarBilete--;

        save();
    }

    private void verificaSpatiuDisponibil() {
        if (numarBilete >= CAPACITATE_MAXIMA) {
            throw new IllegalStateException("S-a atins capacitatea maxima de bilete.");
        }
    }

    private CodBilet genereazaCodBilet(String titluEveniment) {
        String prefix = extragePrefix(titluEveniment);
        int ultimulNumar = 1023;

        for (int i = 0; i < numarBilete; i++) {
            String cod = bilete[i].getCodBilet().getValoare();

            if (cod.startsWith(prefix + "-")) {
                int pozitieUltimaLiniuta = cod.lastIndexOf('-');

                if (pozitieUltimaLiniuta != -1 && pozitieUltimaLiniuta < cod.length() - 1) {
                    String numarText = cod.substring(pozitieUltimaLiniuta + 1);

                    try {
                        int numar = Integer.parseInt(numarText);
                        if (numar > ultimulNumar) {
                            ultimulNumar = numar;
                        }
                    } catch (NumberFormatException e) {
                        // ignoram codurile invalide
                    }
                }
            }
        }

        return new CodBilet(prefix + "-" + (ultimulNumar + 1));
    }

    private String extragePrefix(String titluEveniment) {
        String text = titluEveniment.trim().toUpperCase();
        String doarLitere = "";

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                doarLitere = doarLitere + c;
            }
        }

        if (doarLitere.length() >= 2) {
            return doarLitere.substring(0, 2);
        }

        if (doarLitere.length() == 1) {
            return doarLitere + "X";
        }

        return "EV";
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

                if (p.length < 8) {
                    continue;
                }

                int id = Integer.parseInt(p[0]);
                String tipBilet = p[1];
                String codText = p[2];
                int evenimentId = Integer.parseInt(p[3]);
                int clientId = Integer.parseInt(p[4]);
                String seatCode = p[5];
                double pret = Double.parseDouble(p[6]);
                StatusBilet status = StatusBilet.valueOf(p[7]);

                if (seatCode.length() == 0) {
                    seatCode = null;
                }

                Bilet bilet;

                if (seatCode == null || seatCode.trim().length() == 0) {
                    bilet = new BiletFaraLoc(
                            id,
                            new CodBilet(codText),
                            evenimentId,
                            clientId,
                            tipBilet,
                            pret,
                            status);
                } else {
                    bilet = new BiletCuLoc(
                            id,
                            new CodBilet(codText),
                            evenimentId,
                            clientId,
                            seatCode,
                            tipBilet,
                            pret,
                            status);
                }

                bilete[numarBilete] = bilet;
                numarBilete++;

                String cod = valoareCodBilet(bilet);
                if (!cod.equals("")) {
                    coduriBilete.add(cod);
                }

                if (id >= nextId) {
                    nextId = id + 1;
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Eroare la incarcarea biletelor: " + e.getMessage());
        }
    }

    private void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(NUME_FISIER));

            writer.write("id;tip;codBilet;evenimentId;clientId;seatCode;pret;status");
            writer.newLine();

            for (int i = 0; i < numarBilete; i++) {
                Bilet bilet = bilete[i];

                String seatCode = "";
                if (bilet instanceof BiletCuLoc) {
                    seatCode = ((BiletCuLoc) bilet).getSeatCode();
                }

                String linie = bilet.getId() + ";" +
                        bilet.getTipBilet() + ";" +
                        bilet.getCodBilet().getValoare() + ";" +
                        bilet.getEvenimentId() + ";" +
                        bilet.getClientId() + ";" +
                        seatCode + ";" +
                        bilet.getPret() + ";" +
                        bilet.getStatus().name();

                writer.write(linie);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Eroare la salvarea biletelor: " + e.getMessage());
        }
    }
}
