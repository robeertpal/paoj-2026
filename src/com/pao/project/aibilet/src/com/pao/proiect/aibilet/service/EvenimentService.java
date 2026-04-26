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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvenimentService {
    private static final EvenimentService INSTANCE = new EvenimentService();

    private static final String FISIER_EVENIMENTE = "events.csv";
    private static final String FISIER_LOCURI = "event_seats.csv";
    private static final String FISIER_TIPURI_BILETE = "event_ticket_types.csv";
    private static final int CAPACITATE_MAXIMA = 1000;

    private final Eveniment[] evenimente;
    private int numarEvenimente;
    private int nextId;

    private final LocatieService locatieService = LocatieService.getInstance();
    private final UtilizatorService utilizatorService = UtilizatorService.getInstance();

    private EvenimentService() {
        this.evenimente = new Eveniment[CAPACITATE_MAXIMA];
        this.numarEvenimente = 0;
        this.nextId = 1;
        load();
    }

    public static EvenimentService getInstance() {
        return INSTANCE;
    }

    private int genereazaId() {
        int id = nextId;
        nextId++;
        return id;
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
                genereazaId(),
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

        evenimente[numarEvenimente] = eveniment;
        numarEvenimente++;

        save();
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
                genereazaId(),
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

        evenimente[numarEvenimente] = eveniment;
        numarEvenimente++;

        save();
        return eveniment;
    }

    public Eveniment findById(int id) throws EntitateInexistentaException {
        for (int i = 0; i < numarEvenimente; i++) {
            if (evenimente[i].getId() == id) {
                return evenimente[i];
            }
        }

        throw new EntitateInexistentaException("Evenimentul cu id=" + id + " nu exista.");
    }

    public Eveniment[] listAll() {
        List<Eveniment> lista = new ArrayList<Eveniment>();

        for (int i = 0; i < numarEvenimente; i++) {
            lista.add(evenimente[i]);
        }

        Collections.sort(lista);

        Eveniment[] rezultat = new Eveniment[lista.size()];

        for (int i = 0; i < lista.size(); i++) {
            rezultat[i] = lista.get(i);
        }

        return rezultat;
    }

    public Eveniment[] cautaDupaTitlu(String query) {
        String q = "";
        if (query != null) {
            q = query.toLowerCase();
        }

        int count = 0;

        for (int i = 0; i < numarEvenimente; i++) {
            if (evenimente[i].getTitlu().toLowerCase().contains(q)) {
                count++;
            }
        }

        Eveniment[] rezultat = new Eveniment[count];
        int index = 0;

        for (int i = 0; i < numarEvenimente; i++) {
            if (evenimente[i].getTitlu().toLowerCase().contains(q)) {
                rezultat[index] = evenimente[i];
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

        for (int i = 0; i < numarEvenimente; i++) {
            try {
                Locatie locatie = locatieService.findById(evenimente[i].getLocatieId());

                if (locatie.getOras().toLowerCase().equals(orasCautat)) {
                    count++;
                }
            } catch (EntitateInexistentaException e) {
                // ignoram datele inconsistente
            }
        }

        Eveniment[] rezultat = new Eveniment[count];
        int index = 0;

        for (int i = 0; i < numarEvenimente; i++) {
            try {
                Locatie locatie = locatieService.findById(evenimente[i].getLocatieId());

                if (locatie.getOras().toLowerCase().equals(orasCautat)) {
                    rezultat[index] = evenimente[i];
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

        for (int i = 0; i < numarEvenimente; i++) {
            if (evenimente[i].getOrganizatorId() == organizatorId) {
                count++;
            }
        }

        Eveniment[] rezultat = new Eveniment[count];
        int index = 0;

        for (int i = 0; i < numarEvenimente; i++) {
            if (evenimente[i].getOrganizatorId() == organizatorId) {
                rezultat[index] = evenimente[i];
                index++;
            }
        }

        return rezultat;
    }

    public void deleteById(int id) throws EntitateInexistentaException {
        int pozitie = -1;

        for (int i = 0; i < numarEvenimente; i++) {
            if (evenimente[i].getId() == id) {
                pozitie = i;
                break;
            }
        }

        if (pozitie == -1) {
            throw new EntitateInexistentaException("Evenimentul cu id=" + id + " nu exista.");
        }

        for (int i = pozitie; i < numarEvenimente - 1; i++) {
            evenimente[i] = evenimente[i + 1];
        }

        evenimente[numarEvenimente - 1] = null;
        numarEvenimente--;

        save();
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
        save();
    }

    public void actualizeaza(Eveniment evenimentActualizat) throws EntitateInexistentaException {
        for (int i = 0; i < numarEvenimente; i++) {
            if (evenimente[i].getId() == evenimentActualizat.getId()) {
                evenimente[i] = evenimentActualizat;
                save();
                return;
            }
        }
        throw new EntitateInexistentaException("Evenimentul cu id=" + evenimentActualizat.getId() + " nu exista.");
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

    private void load() {
        File fisier = new File(FISIER_EVENIMENTE);

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

                if (p.length < 12) {
                    continue;
                }

                int id = Integer.parseInt(p[0]);
                String tipEveniment = p[1];
                String titlu = p[2];
                String descriere = p[3];
                CategorieEveniment categorie = CategorieEveniment.valueOf(p[4]);
                String dataOraInceput = p[5];
                String dataOraFinal = p[6];
                StatusEveniment status = StatusEveniment.valueOf(p[7]);
                int locatieId = Integer.parseInt(p[8]);
                int organizatorId = Integer.parseInt(p[9]);
                String numeOrganizatie = "";

                if (p.length >= 13) {
                    numeOrganizatie = p[12];
                }

                Eveniment eveniment;

                if (tipEveniment.equals("SEATED")) {
                    int randuri = Integer.parseInt(p[10]);
                    int coloane = Integer.parseInt(p[11]);

                    HartaLocuri hartaLocuri = incarcaHartaLocuriPentruEveniment(id, randuri, coloane);

                    EvenimentCuLocuri cuLocuri = new EvenimentCuLocuri(
                            id,
                            titlu,
                            descriere,
                            categorie,
                            dataOraInceput,
                            dataOraFinal,
                            status,
                            locatieId,
                            organizatorId,
                            numeOrganizatie,
                            hartaLocuri
                    );

                    TipBiletEveniment[] tipuri = incarcaTipuriBiletePentruEveniment(id);
                    cuLocuri.seteazaTipuriBilete(tipuri, tipuri.length);
                    eveniment = cuLocuri;
                } else {
                    int capacitateTotala = Integer.parseInt(p[10]);
                    int locuriDisponibile = Integer.parseInt(p[11]);

                    EvenimentFaraLocuri faraLocuri = new EvenimentFaraLocuri(
                            id,
                            titlu,
                            descriere,
                            categorie,
                            dataOraInceput,
                            dataOraFinal,
                            status,
                            locatieId,
                            organizatorId,
                            numeOrganizatie,
                            capacitateTotala,
                            locuriDisponibile
                    );

                    TipBiletEveniment[] tipuri = incarcaTipuriBiletePentruEveniment(id);
                    faraLocuri.seteazaTipuriBilete(tipuri, tipuri.length);
                    eveniment = faraLocuri;
                }

                evenimente[numarEvenimente] = eveniment;
                numarEvenimente++;

                if (id >= nextId) {
                    nextId = id + 1;
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Eroare la incarcarea evenimentelor: " + e.getMessage());
        }
    }

    private HartaLocuri incarcaHartaLocuriPentruEveniment(int eventId, int randuri, int coloane) {
        LocEveniment[][] matrice = new LocEveniment[randuri][coloane];
        File fisier = new File(FISIER_LOCURI);

        if (!fisier.exists()) {
            return new HartaLocuri(matrice);
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fisier));
            String linie;
            boolean primaLinie = true;

            while ((linie = reader.readLine()) != null) {
                if (linie.trim().length() == 0) {
                    continue;
                }

                if (primaLinie && linie.startsWith("eventId;")) {
                    primaLinie = false;
                    continue;
                }

                primaLinie = false;

                String[] p = linie.split(";", -1);

                if (p.length < 6) {
                    continue;
                }

                int idEvenimentDinFisier = Integer.parseInt(p[0]);

                if (idEvenimentDinFisier == eventId) {
                    int rand = Integer.parseInt(p[1]);
                    int coloana = Integer.parseInt(p[2]);
                    String cod = p[3];
                    String tipBilet = p[4];
                    StatusLoc status = StatusLoc.valueOf(p[5]);

                    matrice[rand][coloana] = new LocEveniment(rand, coloana, cod, tipBilet, status);
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Eroare la incarcarea locurilor pentru evenimentul " + eventId + ": " + e.getMessage());
        }

        return new HartaLocuri(matrice);
    }

    private TipBiletEveniment[] incarcaTipuriBiletePentruEveniment(int eventId) {
        File fisier = new File(FISIER_TIPURI_BILETE);

        if (!fisier.exists()) {
            return new TipBiletEveniment[0];
        }

        int count = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fisier));
            String linie;
            boolean primaLinie = true;

            while ((linie = reader.readLine()) != null) {
                if (linie.trim().length() == 0) {
                    continue;
                }

                if (primaLinie && linie.startsWith("eventId;")) {
                    primaLinie = false;
                    continue;
                }

                primaLinie = false;

                String[] p = linie.split(";", -1);

                if (p.length >= 5 && Integer.parseInt(p[0]) == eventId) {
                    count++;
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Eroare la numararea tipurilor de bilete: " + e.getMessage());
        }

        TipBiletEveniment[] tipuri = new TipBiletEveniment[count];
        int index = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fisier));
            String linie;
            boolean primaLinie = true;

            while ((linie = reader.readLine()) != null) {
                if (linie.trim().length() == 0) {
                    continue;
                }

                if (primaLinie && linie.startsWith("eventId;")) {
                    primaLinie = false;
                    continue;
                }

                primaLinie = false;

                String[] p = linie.split(";", -1);

                if (p.length >= 5 && Integer.parseInt(p[0]) == eventId) {
                    tipuri[index] = new TipBiletEveniment(
                            p[1],
                            Double.parseDouble(p[2]),
                            Integer.parseInt(p[3]),
                            Integer.parseInt(p[4])
                    );
                    index++;
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Eroare la incarcarea tipurilor de bilete: " + e.getMessage());
        }

        return tipuri;
    }

    private void save() {
        try {
            BufferedWriter writerEvenimente = new BufferedWriter(new FileWriter(FISIER_EVENIMENTE));
            BufferedWriter writerLocuri = new BufferedWriter(new FileWriter(FISIER_LOCURI));
            BufferedWriter writerTipuri = new BufferedWriter(new FileWriter(FISIER_TIPURI_BILETE));

            writerEvenimente.write("id;type;titlu;descriere;categorie;dataOraInceput;dataOraFinal;status;locatieId;organizatorId;extra1;extra2;organizatie");
            writerEvenimente.newLine();

            writerLocuri.write("eventId;rand;coloana;cod;tipBilet;status");
            writerLocuri.newLine();

            writerTipuri.write("eventId;tip;pret;stocTotal;stocDisponibil");
            writerTipuri.newLine();

            for (int i = 0; i < numarEvenimente; i++) {
                Eveniment eveniment = evenimente[i];

                if (eveniment instanceof EvenimentCuLocuri) {
                    EvenimentCuLocuri cuLocuri = (EvenimentCuLocuri) eveniment;

                    writerEvenimente.write(
                            eveniment.getId() + ";" +
                                    "SEATED" + ";" +
                                    eveniment.getTitlu() + ";" +
                                    eveniment.getDescriere() + ";" +
                                    eveniment.getCategorie().name() + ";" +
                                    eveniment.getDataOraInceput() + ";" +
                                    eveniment.getDataOraFinal() + ";" +
                                    eveniment.getStatus().name() + ";" +
                                    eveniment.getLocatieId() + ";" +
                                    eveniment.getOrganizatorId() + ";" +
                                    cuLocuri.getHartaLocuri().getRanduri() + ";" +
                                    cuLocuri.getHartaLocuri().getColoane() + ";" +
                                    eveniment.getNumeOrganizatieOrganizator()
                    );
                    writerEvenimente.newLine();

                    LocEveniment[] locuri = cuLocuri.getHartaLocuri().toateLocurile();

                    for (int j = 0; j < locuri.length; j++) {
                        LocEveniment loc = locuri[j];

                        writerLocuri.write(
                                eveniment.getId() + ";" +
                                        loc.getRand() + ";" +
                                        loc.getColoana() + ";" +
                                        loc.getCod() + ";" +
                                        loc.getTipBilet() + ";" +
                                        loc.getStatus().name()
                        );
                        writerLocuri.newLine();
                    }

                    TipBiletEveniment[] tipuri = cuLocuri.getTipuriBilete();

                    for (int j = 0; j < tipuri.length; j++) {
                        TipBiletEveniment tip = tipuri[j];

                        writerTipuri.write(
                                eveniment.getId() + ";" +
                                        tip.getNume() + ";" +
                                        tip.getPret() + ";" +
                                        tip.getStocTotal() + ";" +
                                        tip.getStocDisponibil()
                        );
                        writerTipuri.newLine();
                    }

                } else if (eveniment instanceof EvenimentFaraLocuri) {
                    EvenimentFaraLocuri faraLocuri = (EvenimentFaraLocuri) eveniment;

                    writerEvenimente.write(
                            eveniment.getId() + ";" +
                                    "STANDING" + ";" +
                                    eveniment.getTitlu() + ";" +
                                    eveniment.getDescriere() + ";" +
                                    eveniment.getCategorie().name() + ";" +
                                    eveniment.getDataOraInceput() + ";" +
                                    eveniment.getDataOraFinal() + ";" +
                                    eveniment.getStatus().name() + ";" +
                                    eveniment.getLocatieId() + ";" +
                                    eveniment.getOrganizatorId() + ";" +
                                    faraLocuri.getCapacitateTotala() + ";" +
                                    faraLocuri.getDisponibilitate() + ";" +
                                    eveniment.getNumeOrganizatieOrganizator()
                    );
                    writerEvenimente.newLine();

                    TipBiletEveniment[] tipuri = faraLocuri.getTipuriBilete();

                    for (int j = 0; j < tipuri.length; j++) {
                        TipBiletEveniment tip = tipuri[j];

                        writerTipuri.write(
                                eveniment.getId() + ";" +
                                        tip.getNume() + ";" +
                                        tip.getPret() + ";" +
                                        tip.getStocTotal() + ";" +
                                        tip.getStocDisponibil()
                        );
                        writerTipuri.newLine();
                    }
                }
            }

            writerEvenimente.close();
            writerLocuri.close();
            writerTipuri.close();
        } catch (IOException e) {
            System.out.println("Eroare la salvarea evenimentelor: " + e.getMessage());
        }
    }
}