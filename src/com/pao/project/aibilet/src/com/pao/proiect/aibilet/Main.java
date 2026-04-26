package com.pao.proiect.aibilet;

import com.pao.proiect.aibilet.exception.AutentificareEsuataException;
import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.exception.LocIndisponibilException;
import com.pao.proiect.aibilet.exception.OperatieNepermisaException;
import com.pao.proiect.aibilet.model.AgentCheckIn;
import com.pao.proiect.aibilet.model.Bilet;
import com.pao.proiect.aibilet.model.CategorieEveniment;
import com.pao.proiect.aibilet.model.Client;
import com.pao.proiect.aibilet.model.Comanda;
import com.pao.proiect.aibilet.model.Eveniment;
import com.pao.proiect.aibilet.model.StatusEveniment;
import com.pao.proiect.aibilet.model.EvenimentCuLocuri;
import com.pao.proiect.aibilet.model.EvenimentFaraLocuri;
import com.pao.proiect.aibilet.model.HartaLocuri;
import com.pao.proiect.aibilet.model.LocEveniment;
import com.pao.proiect.aibilet.model.Locatie;
import com.pao.proiect.aibilet.model.Organizator;
import com.pao.proiect.aibilet.model.RolUtilizator;
import com.pao.proiect.aibilet.model.TipBiletEveniment;
import com.pao.proiect.aibilet.model.Utilizator;
import com.pao.proiect.aibilet.service.EvenimentService;
import com.pao.proiect.aibilet.service.LocatieService;
import com.pao.proiect.aibilet.service.TicketingService;
import com.pao.proiect.aibilet.service.UtilizatorService;


import java.util.Scanner;

public class Main {
    private static final String TIP_NEALOCAT = "NEALOCAT";

    private final Scanner scanner;
    private final UtilizatorService utilizatorService;
    private final LocatieService locatieService;
    private final EvenimentService evenimentService;
    private final TicketingService ticketingService;

    public Main() {
        scanner = new Scanner(System.in);
        utilizatorService = UtilizatorService.getInstance();
        locatieService = LocatieService.getInstance();
        evenimentService = EvenimentService.getInstance();
        ticketingService = TicketingService.getInstance();
    }

    public static void main(String[] args) {
        Main aplicatie = new Main();
        aplicatie.run();
    }

    private void run() {
        while (true) {
            afiseazaTitlu("aibilet");
            System.out.println("1. Login");
            System.out.println("2. Inregistrare");
            System.out.println("3. Afiseaza toate evenimentele");
            System.out.println("4. Cauta eveniment dupa titlu");
            System.out.println("0. Iesire");

            int optiune = citesteInt("Alege optiunea: ");

            switch (optiune) {
                case 1:
                    loginFlow();
                    break;
                case 2:
                    registerFlow();
                    break;
                case 3:
                    listAllEvents();
                    break;
                case 4:
                    searchEventsFlow();
                    break;
                case 0:
                    System.out.println("La revedere!");
                    return;
                default:
                    System.out.println("Optiune invalida.");
            }

            pauza();
        }
    }

    private void registerFlow() {
        afiseazaTitluMic("Inregistrare");
        System.out.println("1. Client");
        System.out.println("2. Organizator");

        int optiune = citesteInt("Cum vrei sa te inregistrezi? ");


        switch (optiune) {
            case 1:
                registerClientFlow();
                break;
            case 2:
                registerOrganizerFlow();
                break;
            default:
                System.out.println("Optiune invalida.");
        }
    }

    private void loginFlow() {
        String username = citesteText("Username: ");
        String parola = citesteText("Parola: ");

        try {
            Utilizator utilizator = ticketingService.login(username, parola);
            System.out.println("Autentificare reusita.");

            if (utilizator.getRol() == RolUtilizator.CLIENT) {
                clientMenu((Client) utilizator);
            } else if (utilizator.getRol() == RolUtilizator.ORGANIZATOR) {
                organizerMenu((Organizator) utilizator);
            } else if (utilizator.getRol() == RolUtilizator.ADMIN) {
                adminMenu(utilizator);
            } else if (utilizator.getRol() == RolUtilizator.AGENT_CHECK_IN) {
                agentCheckInMenu((AgentCheckIn) utilizator);
            }
        } catch (AutentificareEsuataException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void registerClientFlow() {
        String username = citesteText("Username: ");
        String parola = citesteText("Parola: ");
        String nume = citesteText("Nume: ");
        String prenume = citesteText("Prenume: ");
        String email = citesteText("Email: ");
        String telefon = citesteText("Telefon: ");

        try {
            Client client = utilizatorService.adaugaClient(username, parola, nume, prenume, email, telefon);
            System.out.println("Client inregistrat cu succes.");
            afiseazaUtilizator(client);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void registerOrganizerFlow() {
        String username = citesteText("Username: ");
        String parola = citesteText("Parola: ");
        String nume = citesteText("Nume: ");
        String prenume = citesteText("Prenume: ");
        String email = citesteText("Email: ");
        String telefon = citesteText("Telefon: ");
        String organizatiiText = citesteText("Organizatii (separate prin virgula): ");

        try {
            String[] organizatii = parseazaListaText(organizatiiText);
            Organizator organizator = utilizatorService.adaugaOrganizator(username, parola, nume, prenume, email, telefon, organizatii);
            System.out.println("Organizator inregistrat cu succes.");
            afiseazaUtilizator(organizator);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void clientMenu(Client client) {
        while (true) {
            afiseazaTitlu("Meniu client");
            System.out.println("1. Afiseaza toate evenimentele");
            System.out.println("2. Cauta eveniment dupa titlu");
            System.out.println("3. Filtreaza si afiseaza evenimentele");
            System.out.println("4. Cumpara bilet");
            System.out.println("5. Biletele mele");
            System.out.println("6. Comenzile mele");
            System.out.println("7. Schimba parola");
            System.out.println("0. Logout");

            int optiune = citesteInt("Alege optiunea: ");

            switch (optiune) {
                case 1:
                    listAllEvents();
                    promptCumparaBilet(client);
                    break;
                case 2:
                    searchEventsFlow();
                    promptCumparaBilet(client);
                    break;
                case 3:
                    filterEventsFlow();
                    promptCumparaBilet(client);
                    break;
                case 4:
                    cumparaBiletFlow(client);
                    break;
                case 5:
                    listClientTickets(client);
                    break;
                case 6:
                    listClientOrders(client);
                    break;
                case 7:
                    schimbaParolaFlow(client);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Optiune invalida.");
            }

            pauza();
        }
    }

    private void organizerMenu(Organizator organizator) {
        while (true) {
            afiseazaTitlu("Meniu organizator");
            System.out.println("1. Adauga eveniment");
            System.out.println("2. Afiseaza evenimentele mele");
            System.out.println("3. Modifica un eveniment de-al meu");
            System.out.println("4. Anuleaza un eveniment de-al meu");
            System.out.println("5. Sterge un eveniment de-al meu");
            System.out.println("6. Schimba parola");
            System.out.println("7. Asigneaza agent check-in la evenimentul meu");
            System.out.println("8. Sterge asignare agent check-in de la evenimentul meu");
            System.out.println("9. Adauga o noua organizatie");
            System.out.println("0. Logout");

            int optiune = citesteInt("Alege optiunea: ");

            switch (optiune) {
                case 1:
                    addEventFlow(organizator);
                    break;
                case 2:
                    listOrganizerEvents(organizator);
                    break;
                case 3:
                    modifyOrganizerEventFlow(organizator);
                    break;
                case 4:
                    cancelOrganizerEventFlow(organizator);
                    break;
                case 5:
                    deleteOrganizerEventFlow(organizator);
                    break;
                case 6:
                    schimbaParolaFlow(organizator);
                    break;
                case 7:
                    assignAgentCheckInToOwnEventFlow(organizator);
                    break;
                case 8:
                    removeAgentCheckInFromOwnEventFlow(organizator);
                    break;
                case 9:
                    adaugaOrganizatieFlow(organizator);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Optiune invalida.");
            }

            pauza();
        }
    }

    private void adaugaOrganizatieFlow(Organizator organizator) {
        String numeNou = citesteText("Numele noii organizatii: ");
        try {
            utilizatorService.adaugaOrganizatiePentruOrganizator(organizator.getId(), numeNou);
            System.out.println("Organizatie adaugata cu succes.");
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private String selecteazaOrganizatie(Organizator organizator) {
        String[] organizatii = organizator.getNumeOrganizatii();
        if (organizatii.length == 1) {
            return organizatii[0];
        }

        System.out.println("Am identificat in sistem mai multe organizatii:");
        for (int i = 0; i < organizatii.length; i++) {
            System.out.println((i + 1) + ". " + organizatii[i]);
        }

        while (true) {
            int alegere = citesteInt("Alege organizatia in numele careia publici (1-" + organizatii.length + "): ");
            if (alegere >= 1 && alegere <= organizatii.length) {
                return organizatii[alegere - 1];
            }
            System.out.println("Alegere invalida.");
        }
    }

    private void addEventFlow(Organizator organizator) {
        boolean cuLocuri = citesteBoolean("Evenimentul este cu locuri? (da/nu): ");

        if (cuLocuri) {
            publishSeatedEventFlow(organizator);
        } else {
            publishStandingEventFlow(organizator);
        }
    }

    private void adminMenu(Utilizator admin) {
        while (true) {
            afiseazaTitlu("Meniu admin");
            System.out.println("1. Adauga locatie");
            System.out.println("2. Afiseaza locatii");
            System.out.println("3. Afiseaza utilizatori");
            System.out.println("4. Sterge utilizator");
            System.out.println("5. Afiseaza toate evenimentele");
            System.out.println("6. Adauga agent check-in");
            System.out.println("7. Asigneaza agent check-in la eveniment");
            System.out.println("8. Sterge asignare agent check-in de la eveniment");
            System.out.println("9. Schimba parola");
            System.out.println("0. Logout");

            int optiune = citesteInt("Alege optiunea: ");

            switch (optiune) {
                case 1:
                    addLocationFlow();
                    break;
                case 2:
                    listLocations();
                    break;
                case 3:
                    listUsers();
                    break;
                case 4:
                    deleteUserFlow(admin);
                    break;
                case 5:
                    listAllEvents();
                    break;
                case 6:
                    addAgentCheckInFlow();
                    break;
                case 7:
                    assignAgentCheckInToEventFlow();
                    break;
                case 8:
                    removeAgentCheckInFromEventFlow();
                    break;
                case 9:
                    schimbaParolaFlow(admin);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Optiune invalida.");
            }

            pauza();
        }
    }

    private void listAllEvents() {
        Eveniment[] evenimente = evenimentService.listAll();

        if (evenimente.length == 0) {
            System.out.println("Nu exista evenimente.");
            return;
        }

        for (int i = 0; i < evenimente.length; i++) {
            afiseazaEveniment(evenimente[i]);
        }
    }

    private void searchEventsFlow() {
        String query = citesteText("Titlu sau fragment de titlu: ");
        Eveniment[] rezultate = evenimentService.cautaDupaTitlu(query);

        if (rezultate.length == 0) {
            System.out.println("Nu s-au gasit evenimente.");
            return;
        }

        for (int i = 0; i < rezultate.length; i++) {
            afiseazaEveniment(rezultate[i]);
        }
    }

    private void filterEventsFlow() {
        System.out.println("1. Filtreaza dupa oras");
        System.out.println("2. Filtreaza dupa categorie");
        int optiune = citesteInt("Alege un criteriu de filtrare: ");

        if (optiune == 1) {
            filterEventsByCityFlow();
        } else if (optiune == 2) {
            filterEventsByCategoryFlow();
        } else {
            System.out.println("Criteriu invalid.");
        }
    }

    private void filterEventsByCityFlow() {
        String oras = citesteText("Oras: ").toLowerCase();
        Eveniment[] toate = evenimentService.listAll();

        int count = 0;
        for (int i = 0; i < toate.length; i++) {
            try {
                Locatie locatie = locatieService.findById(toate[i].getLocatieId());
                if (locatie.getOras().toLowerCase().equals(oras)) {
                    count++;
                }
            } catch (EntitateInexistentaException e) {
                // ignor
            }
        }

        if (count == 0) {
            System.out.println("Nu exista evenimente pentru orasul ales.");
            return;
        }

        Eveniment[] rezultate = new Eveniment[count];
        int index = 0;

        for (int i = 0; i < toate.length; i++) {
            try {
                Locatie locatie = locatieService.findById(toate[i].getLocatieId());
                if (locatie.getOras().toLowerCase().equals(oras)) {
                    rezultate[index] = toate[i];
                    index++;
                }
            } catch (EntitateInexistentaException e) {
                // ignor
            }
        }

        for (int i = 0; i < rezultate.length; i++) {
            afiseazaEveniment(rezultate[i]);
        }
    }

    private void filterEventsByCategoryFlow() {
        CategorieEveniment categorie = citesteCategorie();
        Eveniment[] toate = evenimentService.listAll();

        int count = 0;
        for (int i = 0; i < toate.length; i++) {
            if (toate[i].getCategorie() == categorie) {
                count++;
            }
        }

        if (count == 0) {
            System.out.println("Nu exista evenimente pentru categoria aleasa.");
            return;
        }

        Eveniment[] rezultate = new Eveniment[count];
        int index = 0;

        for (int i = 0; i < toate.length; i++) {
            if (toate[i].getCategorie() == categorie) {
                rezultate[index] = toate[i];
                index++;
            }
        }

        for (int i = 0; i < rezultate.length; i++) {
            afiseazaEveniment(rezultate[i]);
        }
    }

    private void promptCumparaBilet(Client client) {
        System.out.println();
        boolean vreaSaCumpere = citesteBoolean("Doresti sa achizitionezi un bilet? (da/nu): ");
        if (vreaSaCumpere) {
            cumparaBiletFlow(client);
        }
    }

    private void cumparaBiletFlow(Client client) {
        listAllEvents();

        int evenimentId = citesteInt("ID eveniment: ");

        try {
            Eveniment eveniment = evenimentService.findById(evenimentId);

            if (eveniment instanceof EvenimentCuLocuri) {
                cumparaBiletCuLocFlow(client, (EvenimentCuLocuri) eveniment);
            } else if (eveniment instanceof EvenimentFaraLocuri) {
                cumparaBiletFaraLocFlow(client, (EvenimentFaraLocuri) eveniment);
            }
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void cumparaBiletCuLocFlow(Client client, EvenimentCuLocuri eveniment) {
        try {
            System.out.println(evenimentService.afiseazaHarta(eveniment.getId()));
            afiseazaLegendaTipuriPentruCumparare(eveniment.getTipuriBilete());

            String[] locuriAlese = new String[100];
            int numarLocuri = 0;

            do {
                String codLoc = citesteText("Cod loc (ex: A1): ");
                locuriAlese[numarLocuri] = codLoc;
                numarLocuri++;
                System.out.println("Loc '" + codLoc + "' adaugat. Total selectate: " + numarLocuri);
            } while (citesteBoolean("Doresti sa mai adaugi un loc? (da/nu): "));

            System.out.println("Locuri selectate: " + numarLocuri);
            for (int i = 0; i < numarLocuri; i++) {
                System.out.println("  " + (i + 1) + ". " + locuriAlese[i]);
            }

            boolean confirmare = citesteBoolean("Esti sigur ca vrei sa achizitionezi aceste bilete? (da/nu): ");
            if (!confirmare) {
                System.out.println("Achizitia a fost anulata.");
                return;
            }

            for (int i = 0; i < numarLocuri; i++) {
                try {
                    Bilet bilet = ticketingService.cumparaBiletCuLoc(client.getId(), eveniment.getId(), locuriAlese[i]);
                    System.out.println("Bilet cumparat cu succes pentru locul " + locuriAlese[i] + ".");
                    afiseazaBilet(bilet);
                } catch (LocIndisponibilException e) {
                    System.out.println("Eroare loc '" + locuriAlese[i] + "': " + e.getMessage());
                } catch (OperatieNepermisaException e) {
                    System.out.println("Eroare loc '" + locuriAlese[i] + "': " + e.getMessage());
                }
            }

        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void cumparaBiletFaraLocFlow(Client client, EvenimentFaraLocuri eveniment) {
        TipBiletEveniment[] tipuri = eveniment.getTipuriBilete();

        if (tipuri.length == 0) {
            System.out.println("Evenimentul nu are tipuri de bilete definite.");
            return;
        }

        System.out.println("Tipuri de bilete disponibile:");
        for (int i = 0; i < tipuri.length; i++) {
            System.out.println((i + 1) + ". " + tipuri[i].getNume()
                    + " - pret=" + tipuri[i].getPret()
                    + " - disponibile=" + tipuri[i].getStocDisponibil());
        }

        int[] cantitatiCitite = new int[tipuri.length];
        int numarTipuriSelectate = 0;

        do {
            for (int i = 0; i < tipuri.length; i++) {
                System.out.println((i + 1) + ". " + tipuri[i].getNume());
            }
            int alegere = citesteInt("Alege tipul (1-" + tipuri.length + "): ");
            if (alegere < 1 || alegere > tipuri.length) {
                System.out.println("Alegere invalida.");
                continue;
            }
            int idx = alegere - 1;
            cantitatiCitite[idx]++;
            numarTipuriSelectate = 0;
            for (int i = 0; i < tipuri.length; i++) {
                if (cantitatiCitite[i] > 0) numarTipuriSelectate++;
            }
            System.out.println("Bilet " + tipuri[idx].getNume() + " adaugat. Total selectat: " + cantitatiCitite[idx]);
        } while (citesteBoolean("Doresti sa mai adaugi un bilet? (da/nu): "));

        if (numarTipuriSelectate == 0) {
            System.out.println("Nu ai selectat niciun bilet.");
            return;
        }

        String[] numeTipuri = new String[numarTipuriSelectate];
        int[] cantitati = new int[numarTipuriSelectate];
        int index = 0;
        for (int i = 0; i < tipuri.length; i++) {
            if (cantitatiCitite[i] > 0) {
                numeTipuri[index] = tipuri[i].getNume();
                cantitati[index] = cantitatiCitite[i];
                index++;
            }
        }

        System.out.println("Rezumat comanda:");
        for (int i = 0; i < numarTipuriSelectate; i++) {
            System.out.println("  " + cantitati[i] + "x " + numeTipuri[i]);
        }

        try {
            boolean confirmare = citesteBoolean("Esti sigur ca vrei sa achizitionezi biletele selectate? (da/nu): ");
            if (!confirmare) {
                System.out.println("Achizitia a fost anulata.");
                return;
            }

            Bilet[] bilete = ticketingService.cumparaBileteFaraLocCuTipuri(
                    client.getId(),
                    eveniment.getId(),
                    numeTipuri,
                    cantitati);

            System.out.println("Bilete cumparate cu succes.");
            for (int i = 0; i < bilete.length; i++) {
                afiseazaBilet(bilete[i]);
            }
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (OperatieNepermisaException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void listClientTickets(Client client) {
        Bilet[] bilete = ticketingService.getBileteClient(client.getId());

        if (bilete.length == 0) {
            System.out.println("Nu ai bilete cumparate.");
            return;
        }

        for (int i = 0; i < bilete.length; i++) {
            afiseazaBilet(bilete[i]);
        }
    }

    private void listClientOrders(Client client) {
        Comanda[] comenzi = ticketingService.getComenziClient(client.getId());

        if (comenzi.length == 0) {
            System.out.println("Nu ai comenzi.");
            return;
        }

        for (int i = 0; i < comenzi.length; i++) {
            afiseazaComanda(comenzi[i]);
        }
    }

    private void publishSeatedEventFlow(Organizator organizator) {
        try {
            int randuri = citesteInt("Numar linii: ");
            int coloane = citesteInt("Numar coloane: ");

            if (randuri <= 0 || coloane <= 0) {
                System.out.println("Numarul de linii si coloane trebuie sa fie pozitiv.");
                return;
            }

            String titlu = citesteText("Titlu: ");
            String descriere = citesteText("Descriere: ");
            CategorieEveniment categorie = citesteCategorie();
            String dataOraInceput = citesteDataOraText("Data si ora inceput (yyyy-MM-dd HH:mm): ");
            String dataOraFinal = citesteDataOraText("Data si ora final (yyyy-MM-dd HH:mm): ");
            Locatie locatie = selectLocation();

            HartaLocuri hartaLocuri = HartaLocuri.genereazaUniforma(
                    randuri,
                    coloane,
                    TIP_NEALOCAT);

            System.out.println();
            System.out.println("Harta initiala a salii:");
            System.out.println(hartaLocuri.afisareCompacta());

            boolean eliminaLocuri = citesteBoolean("Vrei sa marchezi locuri ca INEXISTENT? (da/nu): ");
            while (eliminaLocuri) {
                String coduriLocuriInexistente = citesteText("Coduri locuri de eliminat (ex: C4 sau C4 C5 D6): ");
                marcheazaLocuriInexistenteDinText(hartaLocuri, coduriLocuriInexistente);

                System.out.println();
                System.out.println("Harta actualizata dupa eliminarea locurilor selectate:");
                System.out.println(hartaLocuri.afisareCompacta());
                eliminaLocuri = citesteBoolean("Mai marchezi un loc ca INEXISTENT? (da/nu): ");
            }

            String[] numeTipuri = new String[50];
            double[] preturiTipuri = new double[50];
            int numarTipuri = 0;

            while (true) {
                String tip = citesteText("Nume tip bilet: ");
                double pret = citesteDouble("Pret pentru " + tip + ": ");
                boolean aplicat = false;

                while (!aplicat) {
                    String pozitii = citesteText(
                            "Unde se aplica tipul (coduri separate prin spatiu/virgula, interval ex: A1-B2, TOATE sau RESTUL): ");

                    try {
                        aplicaTipBiletPePozitii(hartaLocuri, tip, pozitii);
                        aplicat = true;
                    } catch (LocIndisponibilException e) {
                        System.out.println("Eroare la pozitii: " + e.getMessage());
                        System.out.println("Reincearca pozitiile pentru acelasi tip de bilet.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Eroare la pozitii: " + e.getMessage());
                        System.out.println("Reincearca pozitiile pentru acelasi tip de bilet.");
                    }
                }

                System.out.println();
                System.out.println("Harta actualizata dupa aplicarea tipului " + tip + ":");
                System.out.println(hartaLocuri.afisareCompacta());

                int indexTip = indexTipBilet(numeTipuri, numarTipuri, tip);
                if (indexTip == -1) {
                    numeTipuri[numarTipuri] = tip;
                    preturiTipuri[numarTipuri] = pret;
                    numarTipuri++;
                } else {
                    preturiTipuri[indexTip] = pret;
                }

                int ramaseNealocate = numaraLocuriCuTip(hartaLocuri, TIP_NEALOCAT);
                if (ramaseNealocate > 0) {
                    System.out.println("Mai exista " + ramaseNealocate + " locuri fara tip alocat.");

                    boolean continua = citesteBoolean("Vrei sa adaugi alt tip de bilet? (da/nu): ");
                    if (!continua) {
                        System.out.println(
                                "Nu poti finaliza configuratia pana nu aloci tip pentru toate locurile existente.");
                    }
                    continue;
                }

                boolean continua = citesteBoolean("Vrei sa mai adaugi alt tip de bilet? (da/nu): ");
                if (!continua) {
                    break;
                }
            }

            TipBiletEveniment[] tipuriBilete = construiesteTipuriBileteDinHarta(hartaLocuri, numeTipuri, preturiTipuri,
                    numarTipuri);

            Eveniment eveniment = evenimentService.adaugaEvenimentCuLocuri(
                    titlu,
                    descriere,
                    categorie,
                    dataOraInceput,
                    dataOraFinal,
                    locatie.getId(),
                    organizator.getId(),
                    selecteazaOrganizatie(organizator),
                    hartaLocuri,
                    tipuriBilete);

            System.out.println("Eveniment creat cu succes.");
            afiseazaEveniment(eveniment);

        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (LocIndisponibilException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void publishStandingEventFlow(Organizator organizator) {
        try {
            Locatie locatie = selectLocation();

            String titlu = citesteText("Titlu: ");
            String descriere = citesteText("Descriere: ");
            CategorieEveniment categorie = citesteCategorie();
            String dataOraInceput = citesteDataOraText("Data si ora inceput (yyyy-MM-dd HH:mm): ");
            String dataOraFinal = citesteDataOraText("Data si ora final (yyyy-MM-dd HH:mm): ");

            int numarTipuri = citesteInt("Cate tipuri de bilete are evenimentul? ");
            if (numarTipuri <= 0) {
                System.out.println("Numarul de tipuri trebuie sa fie pozitiv.");
                return;
            }

            TipBiletEveniment[] tipuri = new TipBiletEveniment[numarTipuri];

            for (int i = 0; i < numarTipuri; i++) {
                System.out.println("Tip bilet " + (i + 1) + ":");
                String nume = citesteText("Nume tip: ");
                double pret = citesteDouble("Pret: ");
                int stoc = citesteInt("Stoc total: ");
                tipuri[i] = new TipBiletEveniment(nume, pret, stoc, stoc);
            }

            EvenimentFaraLocuri eveniment = evenimentService.adaugaEvenimentFaraLocuri(
                    titlu,
                    descriere,
                    categorie,
                    dataOraInceput,
                    dataOraFinal,
                    locatie.getId(),
                    organizator.getId(),
                    selecteazaOrganizatie(organizator),
                    tipuri);

            System.out.println("Eveniment creat cu succes.");
            afiseazaEveniment(eveniment);

        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void listOrganizerEvents(Organizator organizator) {
        Eveniment[] evenimente = evenimentService.listByOrganizer(organizator.getId());

        if (evenimente.length == 0) {
            System.out.println("Nu ai evenimente publicate.");
            return;
        }

        for (int i = 0; i < evenimente.length; i++) {
            afiseazaEveniment(evenimente[i]);
        }
    }

    private void deleteOrganizerEventFlow(Organizator organizator) {
        listOrganizerEvents(organizator);
        int eventId = citesteInt("ID eveniment de sters: ");

        try {
            Eveniment eveniment = evenimentService.findById(eventId);

            if (eveniment.getOrganizatorId() != organizator.getId()) {
                throw new OperatieNepermisaException("Poti sterge doar evenimentele tale.");
            }

            evenimentService.deleteById(eventId);
            System.out.println("Eveniment sters.");
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (OperatieNepermisaException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void cancelOrganizerEventFlow(Organizator organizator) {
        listOrganizerEvents(organizator);
        int eventId = citesteInt("ID eveniment de anulat: ");

        try {
            Eveniment eveniment = evenimentService.findById(eventId);

            if (eveniment.getOrganizatorId() != organizator.getId()) {
                throw new OperatieNepermisaException("Poti anula doar evenimentele tale.");
            }

            eveniment.setStatus(StatusEveniment.ANULAT);
            evenimentService.actualizeaza(eveniment);
            System.out.println("Eveniment anulat cu succes.");
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (OperatieNepermisaException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void modifyOrganizerEventFlow(Organizator organizator) {
        listOrganizerEvents(organizator);
        int eventId = citesteInt("ID eveniment de modificat: ");

        try {
            Eveniment eveniment = evenimentService.findById(eventId);

            if (eveniment.getOrganizatorId() != organizator.getId()) {
                throw new OperatieNepermisaException("Poti modifica doar evenimentele tale.");
            }

            System.out.println("Titlu curent: " + eveniment.getTitlu());
            if (citesteBoolean("Doresti sa aduci modificari titlului? (da/nu): ")) {
                eveniment.setTitlu(citesteText("Titlu nou: "));
            }

            System.out.println("Descriere curenta: " + eveniment.getDescriere());
            if (citesteBoolean("Doresti sa aduci modificari descrierii? (da/nu): ")) {
                eveniment.setDescriere(citesteText("Descriere noua: "));
            }

            System.out.println("Categorie curenta: " + eveniment.getCategorie());
            if (citesteBoolean("Doresti sa aduci modificari categoriei? (da/nu): ")) {
                eveniment.setCategorie(citesteCategorie());
            }

            System.out.println("Data si ora incepere curenta: " + eveniment.getDataOraInceput());
            if (citesteBoolean("Doresti sa aduci modificari datei de incepere? (da/nu): ")) {
                eveniment.setDataOraInceput(citesteDataOraText("Data si ora noua (yyyy-MM-dd HH:mm): "));
            }

            System.out.println("Data si ora final curenta: " + eveniment.getDataOraFinal());
            if (citesteBoolean("Doresti sa aduci modificari datei de finalizare? (da/nu): ")) {
                eveniment.setDataOraFinal(citesteDataOraText("Data si ora noua (yyyy-MM-dd HH:mm): "));
            }

            if (citesteBoolean("Doresti sa aduci modificari tipurilor de bilete? (da/nu): ")) {
                TipBiletEveniment[] tipuri = eveniment.getTipuriBilete();
                for (int i = 0; i < tipuri.length; i++) {
                    System.out.println("Tip: " + tipuri[i].getNume() + " | Pret curent: " + tipuri[i].getPret());
                    if (citesteBoolean("Modifici acest tip? (da/nu): ")) {
                        double pretNou = citesteDouble("Pret nou: ");
                        tipuri[i].setPret(pretNou);
                        if (!eveniment.esteCuLocuri()) {
                            int stocExtra = citesteInt("Stoc extra de adaugat (0 pt niciunul): ");
                            if (stocExtra > 0) {
                                tipuri[i].setStocTotal(tipuri[i].getStocTotal() + stocExtra);
                                tipuri[i].setStocDisponibil(tipuri[i].getStocDisponibil() + stocExtra);
                            }
                        }
                    }
                }
                eveniment.seteazaTipuriBilete(tipuri, tipuri.length);
            }

            if (eveniment instanceof EvenimentCuLocuri) {
                EvenimentCuLocuri cuLocuri = (EvenimentCuLocuri) eveniment;
                if (citesteBoolean("Doresti sa aduci modificari hartii de locuri? (da/nu): ")) {
                    HartaLocuri harta = cuLocuri.getHartaLocuri();
                    System.out.println(harta.afisareCompacta());
                    
                    boolean eliminaLocuri = citesteBoolean("Vrei sa marchezi locuri ca INEXISTENT? (da/nu): ");
                    while (eliminaLocuri) {
                        String coduriLocuriInexistente = citesteText("Coduri locuri de eliminat (ex: C4 sau C4 C5 D6): ");
                        try {
                            marcheazaLocuriInexistenteDinText(harta, coduriLocuriInexistente);
                        } catch (Exception e) {
                            System.out.println("Eroare: " + e.getMessage());
                        }
                        System.out.println(harta.afisareCompacta());
                        eliminaLocuri = citesteBoolean("Mai marchezi un loc ca INEXISTENT? (da/nu): ");
                    }
                    
                    boolean schimbaTipLoc = citesteBoolean("Vrei sa schimbi tipul de bilet pentru anumite locuri? (da/nu): ");
                    while (schimbaTipLoc) {
                        String tip = citesteText("Nume tip bilet (ex: VIP, STANDARD): ");
                        String pozitii = citesteText("Coduri locuri (ex: A1-B2, C4): ");
                        try {
                            aplicaTipBiletPePozitii(harta, tip, pozitii);
                        } catch (Exception e) {
                            System.out.println("Eroare: " + e.getMessage());
                        }
                        System.out.println(harta.afisareCompacta());
                        schimbaTipLoc = citesteBoolean("Mai schimbi tipul de bilet pentru alte locuri? (da/nu): ");
                    }
                }
            }

            evenimentService.actualizeaza(eveniment);
            System.out.println("Evenimentul a fost modificat si salvat cu succes.");
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (OperatieNepermisaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare de validare: " + e.getMessage());
        }
    }

    private void addLocationFlow() {
        String denumire = citesteText("Denumire locatie: ");
        String oras = citesteText("Oras: ");
        String adresa = citesteText("Adresa: ");
        boolean suportaLocuri = citesteBoolean("Suporta locuri alocate? (da/nu): ");

        try {
            Locatie locatie = locatieService.adaugaLocatie(
                    denumire, oras, adresa, suportaLocuri);
            System.out.println("Locatie adaugata cu succes.");
            afiseazaLocatie(locatie);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void listLocations() {
        Locatie[] locatii = locatieService.listAll();

        if (locatii.length == 0) {
            System.out.println("Nu exista locatii.");
            return;
        }

        for (int i = 0; i < locatii.length; i++) {
            afiseazaLocatie(locatii[i]);
        }
    }

    private void listUsers() {
        Utilizator[] utilizatori = utilizatorService.listAll();

        if (utilizatori.length == 0) {
            System.out.println("Nu exista utilizatori.");
            return;
        }

        for (int i = 0; i < utilizatori.length; i++) {
            afiseazaUtilizator(utilizatori[i]);
        }
    }

    private void deleteUserFlow(Utilizator admin) {
        listUsers();
        int userId = citesteInt("ID utilizator de sters: ");

        try {
            if (admin.getId() == userId) {
                throw new OperatieNepermisaException("Adminul curent nu se poate sterge singur.");
            }

            Utilizator utilizator = utilizatorService.findById(userId);

            if (utilizator.getRol() == RolUtilizator.ORGANIZATOR) {
                Eveniment[] evenimente = evenimentService.listByOrganizer(userId);
                if (evenimente.length > 0) {
                    throw new OperatieNepermisaException("Organizatorul are evenimente publicate.");
                }
            }

            utilizatorService.deleteById(userId);
            System.out.println("Utilizator sters.");

        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (OperatieNepermisaException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void addAgentCheckInFlow() {
        String username = citesteText("Username: ");
        String parola = citesteText("Parola: ");
        String nume = citesteText("Nume: ");
        String prenume = citesteText("Prenume: ");
        String email = citesteText("Email: ");
        String telefon = citesteText("Telefon: ");

        try {
            AgentCheckIn agent = utilizatorService.adaugaAgentCheckIn(username, parola, nume, prenume, email, telefon);
            System.out.println("Agent check-in inregistrat cu succes.");
            afiseazaUtilizator(agent);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void assignAgentCheckInToEventFlow() {
        listAgentsCheckIn();
        listAllEvents();

        int agentId = citesteInt("ID agent check-in: ");
        int evenimentId = citesteInt("ID eveniment: ");

        try {
            evenimentService.findById(evenimentId);
            utilizatorService.asigneazaAgentLaEveniment(agentId, evenimentId);
            System.out.println("Agent asignat la eveniment cu succes.");
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void removeAgentCheckInFromEventFlow() {
        listAgentsCheckIn();
        listAllEvents();

        int agentId = citesteInt("ID agent check-in: ");
        int evenimentId = citesteInt("ID eveniment: ");

        try {
            utilizatorService.stergeAsignareAgentLaEveniment(agentId, evenimentId);
            System.out.println("Asignare stearsa cu succes.");
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void listAgentsCheckIn() {
        Utilizator[] utilizatori = utilizatorService.listAll();
        boolean existaAgenti = false;

        afiseazaTitluMic("Agenti Check-In");

        for (int i = 0; i < utilizatori.length; i++) {
            if (utilizatori[i].getRol() == RolUtilizator.AGENT_CHECK_IN) {
                existaAgenti = true;
                afiseazaUtilizator(utilizatori[i]);
            }
        }

        if (!existaAgenti) {
            System.out.println("Nu exista agenti check-in inregistrati.");
        }
    }

    private void assignAgentCheckInToOwnEventFlow(Organizator organizator) {
        listAgentsCheckIn();
        listOrganizerEvents(organizator);

        int agentId = citesteInt("ID agent check-in: ");
        int evenimentId = citesteInt("ID eveniment al tau: ");

        try {
            Eveniment eveniment = evenimentService.findById(evenimentId);

            if (eveniment.getOrganizatorId() != organizator.getId()) {
                throw new OperatieNepermisaException("Poti asigna agent doar la evenimentele tale.");
            }

            utilizatorService.asigneazaAgentLaEveniment(agentId, evenimentId);
            System.out.println("Agent asignat la eveniment cu succes.");
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (OperatieNepermisaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void removeAgentCheckInFromOwnEventFlow(Organizator organizator) {
        listAgentsCheckIn();
        listOrganizerEvents(organizator);

        int agentId = citesteInt("ID agent check-in: ");
        int evenimentId = citesteInt("ID eveniment al tau: ");

        try {
            Eveniment eveniment = evenimentService.findById(evenimentId);

            if (eveniment.getOrganizatorId() != organizator.getId()) {
                throw new OperatieNepermisaException("Poti sterge asignari doar de la evenimentele tale.");
            }

            utilizatorService.stergeAsignareAgentLaEveniment(agentId, evenimentId);
            System.out.println("Asignare stearsa cu succes.");
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (OperatieNepermisaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void agentCheckInMenu(AgentCheckIn agent) {
        while (true) {
            afiseazaTitlu("Meniu agent check-in");
            System.out.println("1. Listeaza evenimentele asignate");
            System.out.println("2. Valideaza bilet dupa cod");
            System.out.println("3. Schimba parola");
            System.out.println("0. Logout");

            int optiune = citesteInt("Alege optiunea: ");

            switch (optiune) {
                case 1:
                    listAssignedEvents(agent);
                    break;
                case 2:
                    validateTicketCheckInFlow(agent);
                    break;
                case 3:
                    schimbaParolaFlow(agent);
                    break;
                case 0:
                    System.out.println("Logout agent check-in.");
                    return;
                default:
                    System.out.println("Optiune invalida.");
            }

            pauza();
        }
    }

    private void listAssignedEvents(AgentCheckIn agent) {
        int[] evenimenteAsignate = agent.getEvenimenteAsignate();

        if (evenimenteAsignate.length == 0) {
            System.out.println("Agentul nu este asignat la niciun eveniment.");
            return;
        }

        System.out.println("Evenimente asignate:");

        for (int i = 0; i < evenimenteAsignate.length; i++) {
            try {
                afiseazaEveniment(evenimentService.findById(evenimenteAsignate[i]));
            } catch (EntitateInexistentaException e) {
                System.out.println("Eveniment inexistent cu id=" + evenimenteAsignate[i]);
            }
        }
    }

    private void validateTicketCheckInFlow(AgentCheckIn agent) {
        String codBilet = citesteText("Cod bilet: ");

        try {
            Bilet bilet = ticketingService.valideazaBiletCheckIn(codBilet, agent);
            System.out.println("Bilet validat cu succes.");
            afiseazaBilet(bilet);
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare la check-in: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare la check-in: " + e.getMessage());
        }
    }

    private void schimbaParolaFlow(Utilizator utilizator) {
        String parolaCurenta = citesteText("Parola curenta: ");
        String parolaNoua = citesteText("Parola noua: ");
        String confirmare = citesteText("Confirma parola noua: ");

        if (!parolaNoua.equals(confirmare)) {
            System.out.println("Parolele noi nu coincid.");
            return;
        }

        try {
            utilizatorService.schimbaParola(utilizator.getId(), parolaCurenta, parolaNoua);
            System.out.println("Parola a fost schimbata cu succes.");
        } catch (EntitateInexistentaException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private Locatie selectLocation() throws EntitateInexistentaException {
        listLocations();
        int locatieId = citesteInt("ID locatie: ");
        return locatieService.findById(locatieId);
    }

    private void afiseazaEveniment(Eveniment eveniment) {
        afiseazaTitluMic("Eveniment");
        System.out.println(eveniment);

        try {
            Locatie locatie = locatieService.findById(eveniment.getLocatieId());
            System.out.println("  Locatie: " + locatie.getDenumire() + ", " + locatie.getOras() + ", " + locatie.getAdresa());
        } catch (EntitateInexistentaException e) {
            System.out.println("  Locatie: necunoscuta");
        }

        TipBiletEveniment[] tipuri = eveniment.getTipuriBilete();
        if (tipuri.length > 0) {
            System.out.println("  Tipuri bilete:");
            for (int i = 0; i < tipuri.length; i++) {
                System.out.println("    - " + tipuri[i].getNume()
                        + " | " + tipuri[i].getPret() + " RON"
                        + " | " + tipuri[i].getStocDisponibil() + " disponibile");
            }
        }
    }

    private void afiseazaLocatie(Locatie locatie) {
        afiseazaTitluMic("Locatie");
        System.out.println(locatie);
    }

    private void afiseazaUtilizator(Utilizator utilizator) {
        afiseazaTitluMic("Utilizator");
        System.out.println(utilizator);
    }

    private void afiseazaBilet(Bilet bilet) {
        afiseazaTitluMic("Bilet");
        System.out.println(bilet);
    }

    private void afiseazaComanda(Comanda comanda) {
        afiseazaTitluMic("Comanda");
        System.out.println(comanda);
    }

    private void afiseazaLegendaTipuriPentruCumparare(TipBiletEveniment[] tipuri) {
        if (tipuri == null || tipuri.length == 0) {
            return;
        }

        System.out.println("Legenda tipuri:");

        for (int i = 0; i < tipuri.length; i++) {
            String numeTip = tipuri[i].getNume();
            char initiala = initialaTip(numeTip);
            System.out.println(initiala + " - " + numeTip + " - pretul RON " + tipuri[i].getPret());
        }

        System.out.println();
    }

    private char initialaTip(String numeTip) {
        if (numeTip == null || numeTip.trim().length() == 0) {
            return '?';
        }

        String text = numeTip.trim().toUpperCase();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                return c;
            }
        }

        return text.charAt(0);
    }

    private void afiseazaTitlu(String titlu) {
        System.out.println();
        System.out.println(titlu);
        System.out.println(" ---------");
    }

    private void afiseazaTitluMic(String titlu) {
        System.out.println();
        System.out.println("--- " + titlu + " ---");
    }

    private String citesteText(String mesaj) {
        System.out.print(mesaj);
        return scanner.nextLine().trim();
    }

    private int citesteInt(String mesaj) {
        while (true) {
            try {
                return Integer.parseInt(citesteText(mesaj));
            } catch (NumberFormatException e) {
                System.out.println("Introdu un numar intreg valid.");
            }
        }
    }

    private double citesteDouble(String mesaj) {
        while (true) {
            try {
                return Double.parseDouble(citesteText(mesaj).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Introdu un numar valid.");
            }
        }
    }

    private boolean citesteBoolean(String mesaj) {
        while (true) {
            String raspuns = citesteText(mesaj).toLowerCase();

            if (raspuns.equals("da") || raspuns.equals("d")) {
                return true;
            }

            if (raspuns.equals("nu") || raspuns.equals("n")) {
                return false;
            }

            System.out.println("Raspuns invalid. Scrie da sau nu.");
        }
    }

    private CategorieEveniment citesteCategorie() {
        while (true) {
            System.out.println("Categorii disponibile:");
            CategorieEveniment[] categorii = CategorieEveniment.values();

            for (int i = 0; i < categorii.length; i++) {
                System.out.println("- " + categorii[i].name());
            }

            String valoare = citesteText("Categorie: ").toUpperCase();

            try {
                return CategorieEveniment.valueOf(valoare);
            } catch (IllegalArgumentException e) {
                System.out.println("Categorie invalida.");
            }
        }
    }

    private String citesteDataOraText(String mesaj) {
        while (true) {
            String valoare = citesteText(mesaj);

            if (valoare.length() == 16 && valoare.charAt(4) == '-' && valoare.charAt(7) == '-'
                    && valoare.charAt(10) == ' ' && valoare.charAt(13) == ':') {
                return valoare;
            }

            System.out.println("Format invalid. Exemplu: 2026-05-10 19:30");
        }
    }

    private String[] parseazaListaText(String text) {
        if (text == null || text.trim().length() == 0) {
            throw new IllegalArgumentException("Trebuie introdusa cel putin o organizatie.");
        }

        String[] parti = text.split(",");
        String[] rezultat = new String[parti.length];
        int index = 0;

        for (int i = 0; i < parti.length; i++) {
            String valoare = parti[i].trim();

            if (valoare.length() > 0) {
                rezultat[index] = valoare;
                index++;
            }
        }

        if (index == 0) {
            throw new IllegalArgumentException("Trebuie introdusa cel putin o organizatie.");
        }

        String[] copie = new String[index];
        for (int i = 0; i < index; i++) {
            copie[i] = rezultat[i];
        }

        return copie;
    }

    private void pauza() {
        System.out.println();
        System.out.print("Apasa ENTER pentru a continua...");
        scanner.nextLine();
    }

    private int indexTipBilet(String[] numeTipuri, int numarTipuri, String tip) {
        for (int i = 0; i < numarTipuri; i++) {
            if (numeTipuri[i].equalsIgnoreCase(tip)) {
                return i;
            }
        }

        return -1;
    }

    private void aplicaTipBiletPePozitii(HartaLocuri hartaLocuri, String tipBilet, String pozitii)
            throws LocIndisponibilException {
        if (tipBilet == null || tipBilet.trim().length() == 0) {
            throw new IllegalArgumentException("Tipul de bilet este obligatoriu.");
        }

        if (pozitii == null || pozitii.trim().length() == 0) {
            throw new IllegalArgumentException("Trebuie specificate pozitiile pentru tipul de bilet.");
        }

        String tip = tipBilet.trim();
        String textPozitii = pozitii.trim();

        if (textPozitii.equalsIgnoreCase("TOATE")) {
            LocEveniment[] locuri = hartaLocuri.toateLocurile();

            for (int i = 0; i < locuri.length; i++) {
                hartaLocuri.seteazaTipPentruLoc(locuri[i].getCod(), tip);
            }

            return;
        }

        if (textPozitii.equalsIgnoreCase("RESTUL") || textPozitii.equalsIgnoreCase("REST")) {
            LocEveniment[] locuri = hartaLocuri.toateLocurile();
            int numarAplicari = 0;

            for (int i = 0; i < locuri.length; i++) {
                if (locuri[i].getTipBilet().equalsIgnoreCase(TIP_NEALOCAT)) {
                    hartaLocuri.seteazaTipPentruLoc(locuri[i].getCod(), tip);
                    numarAplicari++;
                }
            }

            if (numarAplicari == 0) {
                throw new LocIndisponibilException("Nu exista locuri ramase nealocate.");
            }

            return;
        }

        String[] coduri = textPozitii.split("[\\s,]+");
        int numarAplicari = 0;

        for (int i = 0; i < coduri.length; i++) {
            String cod = coduri[i].trim().toUpperCase();

            if (cod.length() > 0) {
                if (cod.contains("-")) {
                    try {
                        numarAplicari = numarAplicari + aplicaTipPeInterval(hartaLocuri, tip, cod);
                    } catch (IllegalArgumentException e) {
                        // Ignoram intervalele invalide si continuam cu restul pozitiilor.
                    }
                } else {
                    try {
                        hartaLocuri.seteazaTipPentruLoc(cod, tip);
                        numarAplicari++;
                    } catch (LocIndisponibilException e) {
                        // Ignoram codurile invalide/inexistente si continuam.
                    }
                }
            }
        }

        if (numarAplicari == 0) {
            throw new LocIndisponibilException("Nu s-a gasit niciun loc valid pentru aplicarea tipului de bilet.");
        }
    }

    private int aplicaTipPeInterval(HartaLocuri hartaLocuri, String tip, String interval) {
        String[] capete = interval.split("-");

        if (capete.length != 2) {
            throw new IllegalArgumentException("Interval invalid: " + interval + ". Format corect: A1-B2");
        }

        int[] start = parseazaCodLoc(capete[0].trim());
        int[] end = parseazaCodLoc(capete[1].trim());

        int randMin = Math.min(start[0], end[0]);
        int randMax = Math.max(start[0], end[0]);
        int colMin = Math.min(start[1], end[1]);
        int colMax = Math.max(start[1], end[1]);
        int numarAplicari = 0;

        for (int r = randMin; r <= randMax; r++) {
            for (int c = colMin; c <= colMax; c++) {
                String cod = codDinPozitie(r, c);

                try {
                    hartaLocuri.seteazaTipPentruLoc(cod, tip);
                    numarAplicari++;
                } catch (LocIndisponibilException e) {
                    // Intr-o sala neregulata, intervalul poate include locuri inexistente; le
                    // ignoram.
                }
            }
        }

        return numarAplicari;
    }

    private int[] parseazaCodLoc(String codLoc) {
        if (codLoc == null || codLoc.length() < 2) {
            throw new IllegalArgumentException("Cod loc invalid: " + codLoc);
        }

        String cod = codLoc.trim().toUpperCase();
        char literaRand = cod.charAt(0);

        if (literaRand < 'A' || literaRand > 'Z') {
            throw new IllegalArgumentException("Cod loc invalid: " + codLoc);
        }

        String numarColoanaText = cod.substring(1);
        int coloana;

        try {
            coloana = Integer.parseInt(numarColoanaText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cod loc invalid: " + codLoc);
        }

        if (coloana <= 0) {
            throw new IllegalArgumentException("Cod loc invalid: " + codLoc);
        }

        int[] rezultat = new int[2];
        rezultat[0] = literaRand - 'A';
        rezultat[1] = coloana;
        return rezultat;
    }

    private String codDinPozitie(int randIndex, int coloana) {
        if (randIndex < 0 || randIndex > 25) {
            throw new IllegalArgumentException("Rand invalid pentru interval: " + randIndex);
        }

        char literaRand = (char) ('A' + randIndex);
        return literaRand + String.valueOf(coloana);
    }

    private int numaraLocuriCuTip(HartaLocuri hartaLocuri, String tipCautat) {
        LocEveniment[] locuri = hartaLocuri.toateLocurile();
        int count = 0;

        for (int i = 0; i < locuri.length; i++) {
            if (locuri[i].getTipBilet().equalsIgnoreCase(tipCautat)) {
                count++;
            }
        }

        return count;
    }

    private void marcheazaLocuriInexistenteDinText(HartaLocuri hartaLocuri, String text)
            throws LocIndisponibilException {
        if (text == null || text.trim().length() == 0) {
            throw new IllegalArgumentException("Trebuie sa introduci cel putin un cod de loc.");
        }

        String[] coduri = text.trim().split("[\\s,]+");

        for (int i = 0; i < coduri.length; i++) {
            String cod = coduri[i].trim().toUpperCase();

            if (cod.length() == 0) {
                continue;
            }

            if (cod.contains("-")) {
                marcheazaIntervalInexistent(hartaLocuri, cod);
            } else {
                hartaLocuri.seteazaLocInexistent(cod);
            }
        }
    }

    private void marcheazaIntervalInexistent(HartaLocuri hartaLocuri, String interval)
            throws LocIndisponibilException {
        String[] capete = interval.split("-");

        if (capete.length != 2) {
            throw new IllegalArgumentException("Interval invalid: " + interval + ". Format corect: A1-B2");
        }

        int[] start = parseazaCodLoc(capete[0].trim());
        int[] end = parseazaCodLoc(capete[1].trim());

        int randMin = Math.min(start[0], end[0]);
        int randMax = Math.max(start[0], end[0]);
        int colMin = Math.min(start[1], end[1]);
        int colMax = Math.max(start[1], end[1]);

        for (int r = randMin; r <= randMax; r++) {
            for (int c = colMin; c <= colMax; c++) {
                String cod = codDinPozitie(r, c);
                hartaLocuri.seteazaLocInexistent(cod);
            }
        }
    }

    private TipBiletEveniment[] construiesteTipuriBileteDinHarta(HartaLocuri hartaLocuri,
            String[] numeTipuri,
            double[] preturiTipuri,
            int numarTipuri) {
        int[] stocuri = new int[numarTipuri];
        LocEveniment[] locuri = hartaLocuri.toateLocurile();

        for (int i = 0; i < locuri.length; i++) {
            int index = indexTipBilet(numeTipuri, numarTipuri, locuri[i].getTipBilet());

            if (index >= 0) {
                stocuri[index] = stocuri[index] + 1;
            }
        }

        TipBiletEveniment[] tipuri = new TipBiletEveniment[numarTipuri];

        for (int i = 0; i < numarTipuri; i++) {
            tipuri[i] = new TipBiletEveniment(numeTipuri[i], preturiTipuri[i], stocuri[i], stocuri[i]);
        }

        return tipuri;
    }

}