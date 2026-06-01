package com.pao.proiect.aibilet.service;

import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.model.AgentCheckIn;
import com.pao.proiect.aibilet.model.Bilet;
import com.pao.proiect.aibilet.model.BiletCuLoc;
import com.pao.proiect.aibilet.model.BiletFaraLoc;
import com.pao.proiect.aibilet.model.CodBilet;
import com.pao.proiect.aibilet.model.LocEveniment;
import com.pao.proiect.aibilet.model.StatusBilet;
import com.pao.proiect.aibilet.model.StatusLoc;
import com.pao.proiect.aibilet.model.TipBiletEveniment;
import com.pao.proiect.aibilet.model.dto.BiletClientView;
import com.pao.proiect.aibilet.repository.BiletRepository;
import com.pao.proiect.aibilet.repository.LocEvenimentRepository;
import com.pao.proiect.aibilet.repository.TipBiletEvenimentRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TicketService {
    private static final TicketService INSTANCE = new TicketService();
    private static final int CAPACITATE_MAXIMA = 5000;

    private final Bilet[] bilete;
    private final Set<String> coduriBilete;
    private final BiletRepository biletRepository = BiletRepository.getInstance();
    private final LocEvenimentRepository locEvenimentRepository = LocEvenimentRepository.getInstance();
    private final TipBiletEvenimentRepository tipBiletEvenimentRepository = TipBiletEvenimentRepository.getInstance();
    private int numarBilete;
    private int nextId;

    private TicketService() {
        this.bilete = new Bilet[CAPACITATE_MAXIMA];
        this.coduriBilete = new HashSet<String>();
        this.numarBilete = 0;
        this.nextId = 1;
    }

    public static TicketService getInstance() {
        return INSTANCE;
    }

    private String valoareCodBilet(Bilet bilet) {
        if (bilet == null || bilet.getCodBilet() == null) {
            return "";
        }

        return bilet.getCodBilet().getValoare();
    }

    public Bilet emiteBilet(int evenimentId, String titluEveniment, int clientId, String seatCode, String tipBilet, double pret, StatusBilet status) {
        incarcaBileteleDinBaza();
        verificaSpatiuDisponibil();

        CodBilet codBilet = genereazaCodBilet(titluEveniment);

        if (coduriBilete.contains(codBilet.getValoare())) {
            throw new IllegalStateException("Codul de bilet exista deja.");
        }

        Bilet bilet;

        if (seatCode == null || seatCode.trim().length() == 0) {
            bilet = new BiletFaraLoc(
                    0,
                    codBilet,
                    evenimentId,
                    clientId,
                    tipBilet,
                    pret,
                    status);
        } else {
            bilet = new BiletCuLoc(
                    0,
                    codBilet,
                    evenimentId,
                    clientId,
                    seatCode,
                    tipBilet,
                    pret,
                    status);
        }

        biletRepository.create(bilet);

        try {
            rezervaResursaBilet(bilet);
        } catch (RuntimeException e) {
            biletRepository.delete(bilet.getId());
            throw e;
        }

        adaugaSauInlocuiesteInCache(bilet);
        coduriBilete.add(codBilet.getValoare());

        return bilet;
    }

    public Bilet[] cautaDupaClient(int clientId) {
        incarcaBileteleDinBaza();
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

    public List<BiletClientView> cautaBileteClientCuEvenimentSiLocatie(int clientId) {
        return biletRepository.findBileteClientCuEvenimentSiLocatie(clientId);
    }

    public Bilet findById(int id) throws EntitateInexistentaException {
        Optional<Bilet> bilet = biletRepository.findById(id);

        if (bilet.isPresent()) {
            adaugaSauInlocuiesteInCache(bilet.get());
            return bilet.get();
        }

        throw new EntitateInexistentaException("Biletul cu id=" + id + " nu exista.");
    }

    public Bilet cautaDupaCod(String codBilet) throws EntitateInexistentaException {
        if (codBilet == null || codBilet.trim().equals("")) {
            throw new EntitateInexistentaException("Codul biletului nu poate fi gol.");
        }

        String codCautat = codBilet.trim();
        Bilet[] toate = listAll();

        for (int i = 0; i < toate.length; i++) {
            if (toate[i] != null &&
                    toate[i].getCodBilet() != null &&
                    toate[i].getCodBilet().getValoare().equalsIgnoreCase(codCautat)) {
                return toate[i];
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
        biletRepository.update(bilet);
        adaugaSauInlocuiesteInCache(bilet);

        return bilet;
    }

    public Bilet[] listAll() {
        incarcaBileteleDinBaza();
        Bilet[] copie = new Bilet[numarBilete];

        for (int i = 0; i < numarBilete; i++) {
            copie[i] = bilete[i];
        }

        return copie;
    }

    public void delete(int id) throws EntitateInexistentaException {
        Bilet bilet = findById(id);
        biletRepository.delete(id);

        elibereazaResursaBilet(bilet);
        coduriBilete.remove(valoareCodBilet(bilet));
        stergeDinCache(id);
    }

    private void rezervaResursaBilet(Bilet bilet) {
        if (bilet instanceof BiletCuLoc) {
            BiletCuLoc biletCuLoc = (BiletCuLoc) bilet;
            Optional<LocEveniment> loc = locEvenimentRepository.findByEvenimentIdAndCod(
                    bilet.getEvenimentId(),
                    biletCuLoc.getSeatCode()
            );

            if (!loc.isPresent()) {
                throw new IllegalStateException("Locul " + biletCuLoc.getSeatCode() + " nu exista pentru evenimentul " + bilet.getEvenimentId() + ".");
            }

            LocEveniment locEveniment = loc.get();

            if (locEveniment.getStatus() != StatusLoc.LIBER) {
                throw new IllegalStateException("Locul " + biletCuLoc.getSeatCode() + " nu este liber.");
            }

            TipBiletEveniment tip = gasesteTipBiletPentruEveniment(bilet.getEvenimentId(), locEveniment.getTipBilet());
            if (tip.getStocDisponibil() <= 0) {
                throw new IllegalStateException("Nu exista stoc disponibil pentru tipul " + locEveniment.getTipBilet() + ".");
            }

            locEveniment.setStatus(StatusLoc.VANDUT);
            locEvenimentRepository.update(locEveniment);

            tip.setStocDisponibil(tip.getStocDisponibil() - 1);
            try {
                tipBiletEvenimentRepository.update(tip);
            } catch (RuntimeException e) {
                locEveniment.setStatus(StatusLoc.LIBER);
                locEvenimentRepository.update(locEveniment);
                throw e;
            }
        } else {
            TipBiletEveniment tip = gasesteTipBiletPentruEveniment(bilet.getEvenimentId(), bilet.getTipBilet());

            if (tip.getStocDisponibil() <= 0) {
                throw new IllegalStateException("Nu exista stoc disponibil pentru tipul " + bilet.getTipBilet() + ".");
            }

            tip.setStocDisponibil(tip.getStocDisponibil() - 1);
            tipBiletEvenimentRepository.update(tip);
        }
    }

    private void elibereazaResursaBilet(Bilet bilet) {
        if (bilet instanceof BiletCuLoc) {
            BiletCuLoc biletCuLoc = (BiletCuLoc) bilet;
            Optional<LocEveniment> loc = locEvenimentRepository.findByEvenimentIdAndCod(
                    bilet.getEvenimentId(),
                    biletCuLoc.getSeatCode()
            );

            if (loc.isPresent()) {
                LocEveniment locEveniment = loc.get();

                if (locEveniment.getStatus() == StatusLoc.VANDUT) {
                    TipBiletEveniment tip = gasesteTipBiletPentruEveniment(bilet.getEvenimentId(), locEveniment.getTipBilet());

                    locEveniment.setStatus(StatusLoc.LIBER);
                    locEvenimentRepository.update(locEveniment);

                    tip.setStocDisponibil(tip.getStocDisponibil() + 1);
                    try {
                        tipBiletEvenimentRepository.update(tip);
                    } catch (RuntimeException e) {
                        locEveniment.setStatus(StatusLoc.VANDUT);
                        locEvenimentRepository.update(locEveniment);
                        throw e;
                    }
                }
            }
        } else {
            TipBiletEveniment tip = gasesteTipBiletPentruEveniment(bilet.getEvenimentId(), bilet.getTipBilet());
            tip.setStocDisponibil(tip.getStocDisponibil() + 1);
            tipBiletEvenimentRepository.update(tip);
        }
    }

    private TipBiletEveniment gasesteTipBiletPentruEveniment(int evenimentId, String numeTip) {
        List<TipBiletEveniment> tipuri = tipBiletEvenimentRepository.findByEvenimentId(evenimentId);

        for (int i = 0; i < tipuri.size(); i++) {
            if (tipuri.get(i).getNume().equalsIgnoreCase(numeTip)) {
                return tipuri.get(i);
            }
        }

        throw new IllegalStateException("Tipul de bilet " + numeTip + " nu exista pentru evenimentul " + evenimentId + ".");
    }

    private void incarcaBileteleDinBaza() {
        List<Bilet> bileteDinBaza = biletRepository.findAll();
        reseteazaCache(bileteDinBaza);
    }

    private void reseteazaCache(List<Bilet> bileteDinBaza) {
        for (int i = 0; i < bilete.length; i++) {
            bilete[i] = null;
        }

        coduriBilete.clear();
        numarBilete = 0;
        nextId = 1;

        for (int i = 0; i < bileteDinBaza.size(); i++) {
            adaugaSauInlocuiesteInCache(bileteDinBaza.get(i));
        }
    }

    private void verificaSpatiuDisponibil() {
        if (numarBilete >= CAPACITATE_MAXIMA) {
            throw new IllegalStateException("S-a atins capacitatea maxima de bilete.");
        }
    }

    private void adaugaSauInlocuiesteInCache(Bilet bilet) {
        for (int i = 0; i < numarBilete; i++) {
            if (bilete[i] != null && bilete[i].getId() == bilet.getId()) {
                bilete[i] = bilet;
                adaugaCodInCache(bilet);
                actualizeazaNextId(bilet.getId());
                return;
            }
        }

        verificaSpatiuDisponibil();
        bilete[numarBilete] = bilet;
        numarBilete++;
        adaugaCodInCache(bilet);
        actualizeazaNextId(bilet.getId());
    }

    private void stergeDinCache(int id) {
        int pozitie = -1;

        for (int i = 0; i < numarBilete; i++) {
            if (bilete[i] != null && bilete[i].getId() == id) {
                pozitie = i;
                break;
            }
        }

        if (pozitie == -1) {
            return;
        }

        for (int i = pozitie; i < numarBilete - 1; i++) {
            bilete[i] = bilete[i + 1];
        }

        bilete[numarBilete - 1] = null;
        numarBilete--;
    }

    private void adaugaCodInCache(Bilet bilet) {
        String cod = valoareCodBilet(bilet);

        if (!cod.equals("")) {
            coduriBilete.add(cod);
        }
    }

    private void actualizeazaNextId(int id) {
        if (id >= nextId) {
            nextId = id + 1;
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

}
