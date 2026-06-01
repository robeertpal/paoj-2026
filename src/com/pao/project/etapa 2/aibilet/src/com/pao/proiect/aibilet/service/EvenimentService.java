package com.pao.proiect.aibilet.service;

import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.model.CategorieEveniment;
import com.pao.proiect.aibilet.model.Eveniment;
import com.pao.proiect.aibilet.model.EvenimentCuLocuri;
import com.pao.proiect.aibilet.model.EvenimentFaraLocuri;
import com.pao.proiect.aibilet.model.HartaLocuri;
import com.pao.proiect.aibilet.model.LocEveniment;
import com.pao.proiect.aibilet.model.Locatie;
import com.pao.proiect.aibilet.model.Organizator;
import com.pao.proiect.aibilet.model.StatusEveniment;
import com.pao.proiect.aibilet.model.StatusLoc;
import com.pao.proiect.aibilet.model.TipBiletEveniment;
import com.pao.proiect.aibilet.model.Utilizator;
import com.pao.proiect.aibilet.model.dto.EvenimentVanzariView;
import com.pao.proiect.aibilet.repository.EvenimentRepository;
import com.pao.proiect.aibilet.repository.LocEvenimentRepository;
import com.pao.proiect.aibilet.repository.TipBiletEvenimentRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EvenimentService {
    private static final EvenimentService INSTANCE = new EvenimentService();

    private static final String TIP_NEALOCAT = "NEALOCAT";
    private static final int CAPACITATE_MAXIMA = 1000;

    private final Eveniment[] evenimente;
    private int numarEvenimente;

    private final EvenimentRepository evenimentRepository = EvenimentRepository.getInstance();
    private final TipBiletEvenimentRepository tipBiletEvenimentRepository = TipBiletEvenimentRepository.getInstance();
    private final LocEvenimentRepository locEvenimentRepository = LocEvenimentRepository.getInstance();
    private final LocatieService locatieService = LocatieService.getInstance();
    private final UtilizatorService utilizatorService = UtilizatorService.getInstance();

    private EvenimentService() {
        this.evenimente = new Eveniment[CAPACITATE_MAXIMA];
        this.numarEvenimente = 0;
    }

    public static EvenimentService getInstance() {
        return INSTANCE;
    }

    public EvenimentCuLocuri adaugaEvenimentCuLocuri(String titlu, String descriere, CategorieEveniment categorie, String dataOraInceput, String dataOraFinal, int locatieId, int organizatorId, String numeOrganizatie, HartaLocuri hartaLocuri, TipBiletEveniment[] tipuriBilete) throws EntitateInexistentaException {
        locatieService.findById(locatieId);
        verificaSpatiuDisponibil();

        if (hartaLocuri == null) {
            throw new IllegalArgumentException("Harta locurilor nu poate fi null.");
        }

        if (tipuriBilete == null || tipuriBilete.length == 0) {
            throw new IllegalArgumentException("Evenimentul trebuie sa aiba cel putin un tip de bilet.");
        }

        valideazaOrganizatiePentruOrganizator(organizatorId, numeOrganizatie);

        EvenimentCuLocuri eveniment = new EvenimentCuLocuri(
                0,
                titlu,
                descriere,
                categorie,
                dataOraInceput,
                dataOraFinal,
                StatusEveniment.PROGRAMAT,
                locatieId,
                organizatorId,
                numeOrganizatie,
                hartaLocuri
        );

        TipBiletEveniment[] tipuriCopiate = copiazaTipuriBilete(tipuriBilete);
        eveniment.seteazaTipuriBilete(tipuriCopiate, tipuriCopiate.length);

        evenimentRepository.create(eveniment);
        salveazaTipuriBilete(eveniment);
        salveazaLocuriEveniment(eveniment);
        valideazaConsistentaEvenimentCuLocuri(eveniment.getId());
        adaugaSauInlocuiesteInCache(eveniment);
        return eveniment;
    }

    public EvenimentFaraLocuri adaugaEvenimentFaraLocuri(String titlu, String descriere, CategorieEveniment categorie, String dataOraInceput, String dataOraFinal, int locatieId, int organizatorId, String numeOrganizatie, TipBiletEveniment[] tipuriBilete) throws EntitateInexistentaException {
        locatieService.findById(locatieId);
        verificaSpatiuDisponibil();

        if (tipuriBilete == null || tipuriBilete.length == 0) {
            throw new IllegalArgumentException("Evenimentul trebuie sa aiba cel putin un tip de bilet.");
        }

        valideazaOrganizatiePentruOrganizator(organizatorId, numeOrganizatie);

        int capacitateTotala = calculeazaCapacitateTotala(tipuriBilete);

        EvenimentFaraLocuri eveniment = new EvenimentFaraLocuri(
                0,
                titlu,
                descriere,
                categorie,
                dataOraInceput,
                dataOraFinal,
                StatusEveniment.PROGRAMAT,
                locatieId,
                organizatorId,
                numeOrganizatie,
                capacitateTotala,
                capacitateTotala
        );

        TipBiletEveniment[] tipuriCopiate = copiazaTipuriBilete(tipuriBilete);
        eveniment.seteazaTipuriBilete(tipuriCopiate, tipuriCopiate.length);

        evenimentRepository.create(eveniment);
        salveazaTipuriBilete(eveniment);
        adaugaSauInlocuiesteInCache(eveniment);
        return eveniment;
    }

    public Eveniment findById(int id) throws EntitateInexistentaException {
        Optional<Eveniment> eveniment = evenimentRepository.findById(id);

        if (eveniment.isPresent()) {
            Eveniment complet = completeazaEveniment(eveniment.get());
            adaugaSauInlocuiesteInCache(complet);
            return complet;
        }

        throw new EntitateInexistentaException("Evenimentul cu id=" + id + " nu exista.");
    }

    public Eveniment[] listAll() {
        List<Eveniment> lista = new ArrayList<Eveniment>();
        List<Eveniment> evenimenteDinBaza = evenimentRepository.findAll();
        Map<Integer, List<TipBiletEveniment>> tipuriDupaEveniment = tipBiletEvenimentRepository.findAllGroupedByEvenimentId();
        Map<Integer, List<LocEveniment>> locuriDupaEveniment = locEvenimentRepository.findAllGroupedByEvenimentId();

        for (int i = 0; i < evenimenteDinBaza.size(); i++) {
            Eveniment eveniment = evenimenteDinBaza.get(i);
            List<TipBiletEveniment> tipuri = tipuriDupaEveniment.get(eveniment.getId());
            List<LocEveniment> locuri = locuriDupaEveniment.get(eveniment.getId());
            Eveniment complet = completeazaEveniment(eveniment, listaGoalaDacaNull(tipuri), listaGoalaDacaNull(locuri));
            lista.add(complet);
        }

        Collections.sort(lista);

        Eveniment[] rezultat = new Eveniment[lista.size()];

        for (int i = 0; i < lista.size(); i++) {
            rezultat[i] = lista.get(i);
        }

        reseteazaCache(rezultat);
        return rezultat;
    }

    public Eveniment[] cautaDupaTitlu(String query) {
        String q = "";
        if (query != null) {
            q = query.toLowerCase();
        }

        int count = 0;

        Eveniment[] toate = listAll();

        for (int i = 0; i < toate.length; i++) {
            if (toate[i].getTitlu().toLowerCase().contains(q)) {
                count++;
            }
        }

        Eveniment[] rezultat = new Eveniment[count];
        int index = 0;

        for (int i = 0; i < toate.length; i++) {
            if (toate[i].getTitlu().toLowerCase().contains(q)) {
                rezultat[index] = toate[i];
                index++;
            }
        }

        return rezultat;
    }

    public Eveniment[] filtreazaDupaOras(String oras) {
        String orasCautat = "";
        if (oras != null) {
            orasCautat = oras.toLowerCase();
        }

        int count = 0;

        Eveniment[] toate = listAll();

        for (int i = 0; i < toate.length; i++) {
            try {
                Locatie locatie = locatieService.findById(toate[i].getLocatieId());

                if (locatie.getOras().toLowerCase().equals(orasCautat)) {
                    count++;
                }
            } catch (EntitateInexistentaException e) {
                // ignoram datele inconsistente
            }
        }

        Eveniment[] rezultat = new Eveniment[count];
        int index = 0;

        for (int i = 0; i < toate.length; i++) {
            try {
                Locatie locatie = locatieService.findById(toate[i].getLocatieId());

                if (locatie.getOras().toLowerCase().equals(orasCautat)) {
                    rezultat[index] = toate[i];
                    index++;
                }
            } catch (EntitateInexistentaException e) {
                // ignoram datele inconsistente
            }
        }

        return rezultat;
    }

    public Eveniment[] listByOrganizer(int organizatorId) {
        int count = 0;
        Eveniment[] toate = listAll();

        for (int i = 0; i < toate.length; i++) {
            if (toate[i].getOrganizatorId() == organizatorId) {
                count++;
            }
        }

        Eveniment[] rezultat = new Eveniment[count];
        int index = 0;

        for (int i = 0; i < toate.length; i++) {
            if (toate[i].getOrganizatorId() == organizatorId) {
                rezultat[index] = toate[i];
                index++;
            }
        }

        return rezultat;
    }

    public List<EvenimentVanzariView> listEvenimenteCuNumarBileteVandute() {
        return evenimentRepository.findEvenimenteCuNumarBileteVandute();
    }

    public void delete(int id) throws EntitateInexistentaException {
        findById(id);
        evenimentRepository.delete(id);

        stergeDinCache(id);
    }

    public String afiseazaHarta(int evenimentId) throws EntitateInexistentaException {
        Eveniment eveniment = findById(evenimentId);

        if (eveniment instanceof EvenimentCuLocuri) {
            EvenimentCuLocuri cuLocuri = (EvenimentCuLocuri) eveniment;
            return cuLocuri.getHartaLocuri().afisareCompacta();
        }

        return "Evenimentul selectat nu foloseste locuri.";
    }

    public void persist() {
        for (int i = 0; i < numarEvenimente; i++) {
            if (evenimente[i] != null) {
                sincronizeazaEveniment(evenimente[i]);
            }
        }
    }

    public void actualizeaza(Eveniment evenimentActualizat) throws EntitateInexistentaException {
        if (!evenimentRepository.findById(evenimentActualizat.getId()).isPresent()) {
            throw new EntitateInexistentaException("Evenimentul cu id=" + evenimentActualizat.getId() + " nu exista.");
        }

        sincronizeazaEveniment(evenimentActualizat);
        if (evenimentActualizat instanceof EvenimentCuLocuri) {
            valideazaConsistentaEvenimentCuLocuri(evenimentActualizat.getId());
        }
        adaugaSauInlocuiesteInCache(evenimentActualizat);
    }

    private Eveniment completeazaEveniment(Eveniment eveniment) {
        List<TipBiletEveniment> tipuriDinBaza = tipBiletEvenimentRepository.findByEvenimentId(eveniment.getId());
        List<LocEveniment> locuriDinBaza = new ArrayList<LocEveniment>();

        if (eveniment instanceof EvenimentCuLocuri) {
            locuriDinBaza = locEvenimentRepository.findByEvenimentId(eveniment.getId());
        }

        return completeazaEveniment(eveniment, tipuriDinBaza, locuriDinBaza);
    }

    private Eveniment completeazaEveniment(Eveniment eveniment, List<TipBiletEveniment> tipuriDinBaza, List<LocEveniment> locuriDinBaza) {
        TipBiletEveniment[] tipuri = new TipBiletEveniment[tipuriDinBaza.size()];

        for (int i = 0; i < tipuriDinBaza.size(); i++) {
            tipuri[i] = tipuriDinBaza.get(i);
        }

        eveniment.seteazaTipuriBilete(tipuri, tipuri.length);

        if (eveniment instanceof EvenimentCuLocuri) {
            EvenimentCuLocuri cuLocuri = (EvenimentCuLocuri) eveniment;
            cuLocuri.setHartaLocuri(construiesteHartaLocuriDinLista(locuriDinBaza));
            tipuri = calculeazaStocuriTipuriDinHarta(cuLocuri, tipuri);
            cuLocuri.seteazaTipuriBilete(tipuri, tipuri.length);
        }

        return eveniment;
    }

    private HartaLocuri construiesteHartaLocuriDinBaza(int evenimentId) {
        List<LocEveniment> locuriDinBaza = locEvenimentRepository.findByEvenimentId(evenimentId);
        return construiesteHartaLocuriDinLista(locuriDinBaza);
    }

    private HartaLocuri construiesteHartaLocuriDinLista(List<LocEveniment> locuriDinBaza) {
        if (locuriDinBaza.isEmpty()) {
            return new HartaLocuri(new LocEveniment[0][0]);
        }

        int maxRand = 0;
        int maxColoana = 0;

        for (int i = 0; i < locuriDinBaza.size(); i++) {
            LocEveniment loc = locuriDinBaza.get(i);

            if (loc.getRand() > maxRand) {
                maxRand = loc.getRand();
            }

            if (loc.getColoana() > maxColoana) {
                maxColoana = loc.getColoana();
            }
        }

        LocEveniment[][] matrice = new LocEveniment[maxRand + 1][maxColoana + 1];

        for (int i = 0; i < locuriDinBaza.size(); i++) {
            LocEveniment loc = locuriDinBaza.get(i);
            matrice[loc.getRand()][loc.getColoana()] = loc;
        }

        return new HartaLocuri(matrice);
    }

    private <T> List<T> listaGoalaDacaNull(List<T> lista) {
        if (lista == null) {
            return new ArrayList<T>();
        }

        return lista;
    }

    private void sincronizeazaEveniment(Eveniment eveniment) {
        evenimentRepository.update(eveniment);
        salveazaTipuriBilete(eveniment);

        if (eveniment instanceof EvenimentCuLocuri) {
            salveazaLocuriEveniment((EvenimentCuLocuri) eveniment);
        }
    }

    private void salveazaTipuriBilete(Eveniment eveniment) {
        TipBiletEveniment[] tipuri = eveniment.getTipuriBilete();

        if (eveniment instanceof EvenimentCuLocuri) {
            tipuri = calculeazaStocuriTipuriDinHarta((EvenimentCuLocuri) eveniment, tipuri);
            eveniment.seteazaTipuriBilete(tipuri, tipuri.length);
        }

        List<TipBiletEveniment> existente = tipBiletEvenimentRepository.findByEvenimentId(eveniment.getId());

        for (int i = 0; i < tipuri.length; i++) {
            TipBiletEveniment tip = tipuri[i];
            tip.setEvenimentId(eveniment.getId());

            TipBiletEveniment existent = gasesteTipBiletDupaNume(existente, tip.getNume());

            if (existent == null) {
                tipBiletEvenimentRepository.create(tip);
            } else {
                tip.setId(existent.getId());
                tipBiletEvenimentRepository.update(tip);
            }
        }
    }

    private TipBiletEveniment gasesteTipBiletDupaNume(List<TipBiletEveniment> tipuri, String nume) {
        for (int i = 0; i < tipuri.size(); i++) {
            TipBiletEveniment tip = tipuri.get(i);

            if (tip.getNume().equalsIgnoreCase(nume)) {
                return tip;
            }
        }

        return null;
    }

    private void salveazaLocuriEveniment(EvenimentCuLocuri eveniment) {
        LocEveniment[][] locuri = eveniment.getHartaLocuri().getLocuri();
        List<LocEveniment> existente = locEvenimentRepository.findByEvenimentId(eveniment.getId());
        List<LocEveniment> locuriNoi = new ArrayList<LocEveniment>();

        for (int i = 0; i < locuri.length; i++) {
            for (int j = 0; j < locuri[i].length; j++) {
                LocEveniment loc = locuri[i][j];

                if (!esteLocPersistabil(loc)) {
                    if (loc != null) {
                        LocEveniment existent = gasesteLocDupaCod(existente, loc.getCod());

                        if (existent != null) {
                            locEvenimentRepository.delete(existent.getId());
                        }
                    }

                    continue;
                }

                loc.setEvenimentId(eveniment.getId());

                LocEveniment existent = gasesteLocDupaCod(existente, loc.getCod());

                if (existent != null) {
                    loc.setId(existent.getId());
                    locEvenimentRepository.update(loc);
                } else {
                    locuriNoi.add(loc);
                }
            }
        }

        locEvenimentRepository.createAll(locuriNoi);
    }

    private TipBiletEveniment[] calculeazaStocuriTipuriDinHarta(EvenimentCuLocuri eveniment, TipBiletEveniment[] tipuri) {
        TipBiletEveniment[] rezultat = new TipBiletEveniment[tipuri.length];
        LocEveniment[] locuri = eveniment.getHartaLocuri().toateLocurile();

        for (int i = 0; i < tipuri.length; i++) {
            TipBiletEveniment tip = tipuri[i];
            int stocTotal = 0;
            int stocDisponibil = 0;

            for (int j = 0; j < locuri.length; j++) {
                LocEveniment loc = locuri[j];

                if (esteLocPersistabil(loc) && loc.getTipBilet().equalsIgnoreCase(tip.getNume())) {
                    stocTotal++;

                    if (loc.getStatus() == StatusLoc.LIBER) {
                        stocDisponibil++;
                    }
                }
            }

            TipBiletEveniment tipRecalculat = new TipBiletEveniment(
                    tip.getId(),
                    eveniment.getId(),
                    tip.getNume(),
                    tip.getPret(),
                    stocTotal,
                    stocDisponibil
            );
            rezultat[i] = tipRecalculat;
        }

        return rezultat;
    }

    private boolean esteLocPersistabil(LocEveniment loc) {
        if (loc == null || loc.getStatus() == StatusLoc.INEXISTENT) {
            return false;
        }

        String tipBilet = loc.getTipBilet();

        return tipBilet != null &&
                tipBilet.trim().length() > 0 &&
                !tipBilet.trim().equalsIgnoreCase(TIP_NEALOCAT);
    }

    private LocEveniment gasesteLocDupaCod(List<LocEveniment> locuri, String cod) {
        for (int i = 0; i < locuri.size(); i++) {
            LocEveniment loc = locuri.get(i);

            if (loc.getCod().equalsIgnoreCase(cod)) {
                return loc;
            }
        }

        return null;
    }

    private void valideazaConsistentaEvenimentCuLocuri(int evenimentId) {
        String diagnostic = diagnosticEvenimentCuLocuri(evenimentId);

        if (diagnostic.indexOf("consistent=false") >= 0) {
            throw new IllegalStateException("Evenimentul cu locuri " + evenimentId + " este inconsistent.\n" + diagnostic);
        }
    }

    public String diagnosticEvenimentCuLocuri(int evenimentId) {
        List<LocEveniment> locuri = locEvenimentRepository.findByEvenimentId(evenimentId);
        List<TipBiletEveniment> tipuri = tipBiletEvenimentRepository.findByEvenimentId(evenimentId);

        int totalLocuri = locuri.size();
        int totalStoc = 0;
        int totalDisponibil = 0;
        boolean consistent = true;
        StringBuilder raport = new StringBuilder();

        raport.append("Diagnostic eveniment cu locuri ").append(evenimentId).append("\n");
        raport.append("locuri_eveniment.total=").append(totalLocuri).append("\n");
        raport.append("locuri_per_rand:\n");

        int randCurent = -1;
        int countRand = 0;

        for (int i = 0; i < locuri.size(); i++) {
            LocEveniment loc = locuri.get(i);

            if (randCurent == -1) {
                randCurent = loc.getRand();
            }

            if (loc.getRand() != randCurent) {
                raport.append("  rand=").append(randCurent).append(", count=").append(countRand).append("\n");
                randCurent = loc.getRand();
                countRand = 0;
            }

            countRand++;
        }

        if (randCurent != -1) {
            raport.append("  rand=").append(randCurent).append(", count=").append(countRand).append("\n");
        }

        raport.append("tipuri:\n");

        for (int i = 0; i < tipuri.size(); i++) {
            TipBiletEveniment tip = tipuri.get(i);
            int locuriTip = numaraLocuriPentruTip(locuri, tip.getNume());
            int locuriLibereTip = numaraLocuriLiberePentruTip(locuri, tip.getNume());

            totalStoc = totalStoc + tip.getStocTotal();
            totalDisponibil = totalDisponibil + tip.getStocDisponibil();

            if (tip.getStocTotal() != locuriTip || tip.getStocDisponibil() != locuriLibereTip) {
                consistent = false;
            }

            raport.append("  ")
                    .append(tip.getNume())
                    .append(": stoc_total=").append(tip.getStocTotal())
                    .append(", stoc_disponibil=").append(tip.getStocDisponibil())
                    .append(", locuri=").append(locuriTip)
                    .append(", locuri_libere=").append(locuriLibereTip)
                    .append("\n");
        }

        raport.append("sum_stoc_total=").append(totalStoc).append("\n");
        raport.append("sum_stoc_disponibil=").append(totalDisponibil).append("\n");
        raport.append("consistent=").append(consistent && totalStoc == totalLocuri).append("\n");

        return raport.toString();
    }

    private int numaraLocuriPentruTip(List<LocEveniment> locuri, String tip) {
        int count = 0;

        for (int i = 0; i < locuri.size(); i++) {
            if (locuri.get(i).getTipBilet().equalsIgnoreCase(tip)) {
                count++;
            }
        }

        return count;
    }

    private int numaraLocuriLiberePentruTip(List<LocEveniment> locuri, String tip) {
        int count = 0;

        for (int i = 0; i < locuri.size(); i++) {
            LocEveniment loc = locuri.get(i);

            if (loc.getStatus() == StatusLoc.LIBER && loc.getTipBilet().equalsIgnoreCase(tip)) {
                count++;
            }
        }

        return count;
    }

    private void adaugaSauInlocuiesteInCache(Eveniment eveniment) {
        for (int i = 0; i < numarEvenimente; i++) {
            if (evenimente[i] != null && evenimente[i].getId() == eveniment.getId()) {
                evenimente[i] = eveniment;
                return;
            }
        }

        verificaSpatiuDisponibil();
        evenimente[numarEvenimente] = eveniment;
        numarEvenimente++;
    }

    private void reseteazaCache(Eveniment[] lista) {
        for (int i = 0; i < evenimente.length; i++) {
            evenimente[i] = null;
        }

        numarEvenimente = 0;

        for (int i = 0; i < lista.length; i++) {
            adaugaSauInlocuiesteInCache(lista[i]);
        }
    }

    private void stergeDinCache(int id) {
        int indexGasit = -1;

        for (int i = 0; i < numarEvenimente; i++) {
            if (evenimente[i] != null && evenimente[i].getId() == id) {
                indexGasit = i;
                break;
            }
        }

        if (indexGasit == -1) {
            return;
        }

        for (int i = indexGasit; i < numarEvenimente - 1; i++) {
            evenimente[i] = evenimente[i + 1];
        }

        evenimente[numarEvenimente - 1] = null;
        numarEvenimente--;
    }

    private void verificaSpatiuDisponibil() {
        if (numarEvenimente >= CAPACITATE_MAXIMA) {
            throw new IllegalStateException("S-a atins capacitatea maxima de evenimente.");
        }
    }

    private int calculeazaCapacitateTotala(TipBiletEveniment[] tipuriBilete) {
        int total = 0;

        for (int i = 0; i < tipuriBilete.length; i++) {
            total = total + tipuriBilete[i].getStocTotal();
        }

        return total;
    }

    private TipBiletEveniment[] copiazaTipuriBilete(TipBiletEveniment[] tipuriBilete) {
        TipBiletEveniment[] copie = new TipBiletEveniment[tipuriBilete.length];

        for (int i = 0; i < tipuriBilete.length; i++) {
            TipBiletEveniment tip = tipuriBilete[i];

            copie[i] = new TipBiletEveniment(
                    tip.getNume(),
                    tip.getPret(),
                    tip.getStocTotal(),
                    tip.getStocDisponibil()
            );
        }

        return copie;
    }

    private void valideazaOrganizatiePentruOrganizator(int organizatorId, String numeOrganizatie) throws EntitateInexistentaException {
        Utilizator utilizator = utilizatorService.findById(organizatorId);

        if (!(utilizator instanceof Organizator)) {
            throw new IllegalArgumentException("Utilizatorul selectat nu este organizator.");
        }

        Organizator org = (Organizator) utilizator;
        String[] organizatii = org.getNumeOrganizatii();
        boolean gasit = false;
        
        for (int i = 0; i < organizatii.length; i++) {
            if (organizatii[i].equalsIgnoreCase(numeOrganizatie)) {
                gasit = true;
                break;
            }
        }
        
        if (!gasit) {
            throw new IllegalArgumentException("Organizatia specificata nu este asociata cu acest organizator.");
        }
    }

}
