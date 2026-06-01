package com.pao.proiect.aibilet.service;

import com.pao.proiect.aibilet.exception.AutentificareEsuataException;
import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.model.Admin;
import com.pao.proiect.aibilet.model.AgentCheckIn;
import com.pao.proiect.aibilet.model.Client;
import com.pao.proiect.aibilet.model.Organizator;
import com.pao.proiect.aibilet.model.RolUtilizator;
import com.pao.proiect.aibilet.model.Utilizator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UtilizatorService {
    private static final UtilizatorService INSTANCE = new UtilizatorService();
    private static final String NUME_FISIER = "users.csv";
    private static final String NUME_FISIER_ASIGNARI_AGENT = "agent_event_assignments.csv";
    private static final int CAPACITATE_MAXIMA = 1000;

    private final Utilizator[] utilizatori;
    private final Map<Integer, Utilizator> utilizatoriDupaId;
    private final Map<String, Utilizator> utilizatoriDupaUsername;
    private final Map<Integer, Set<Integer>> asignariAgentEvenimente;
    private int numarUtilizatori;
    private int nextId;

    private UtilizatorService() {
        this.utilizatori = new Utilizator[CAPACITATE_MAXIMA];
        this.utilizatoriDupaId = new HashMap<Integer, Utilizator>();
        this.utilizatoriDupaUsername = new HashMap<String, Utilizator>();
        this.asignariAgentEvenimente = new HashMap<Integer, Set<Integer>>();
        this.numarUtilizatori = 0;
        this.nextId = 1;
        load();
        loadAsignariAgentEvenimente();
    }

    public static UtilizatorService getInstance() {
        return INSTANCE;
    }

    private int genereazaId() {
        int id = nextId;
        nextId++;
        return id;
    }

    private String cheieUsername(String username) {
        if (username == null) {
            return "";
        }
        return username.trim().toLowerCase();
    }

    private void adaugaInIndex(Utilizator utilizator) {
        if (utilizator != null) {
            utilizatoriDupaId.put(utilizator.getId(), utilizator);
            utilizatoriDupaUsername.put(cheieUsername(utilizator.getUsername()), utilizator);
        }
    }

    private void stergeDinIndex(Utilizator utilizator) {
        if (utilizator != null) {
            utilizatoriDupaId.remove(utilizator.getId());
            utilizatoriDupaUsername.remove(cheieUsername(utilizator.getUsername()));
        }
    }

    public Client adaugaClient(String username, String parola, String nume, String prenume, String email, String telefon) {
        validaUsernameLiber(username);
        verificaSpatiuDisponibil();

        Client client = new Client(genereazaId(), username, parola, nume, prenume, email, telefon);
        utilizatori[numarUtilizatori] = client;
        numarUtilizatori++;
        adaugaInIndex(client);

        save();
        return client;
    }

    public Organizator adaugaOrganizator(String username, String parola, String nume, String prenume, String email, String telefon, String numeOrganizatie) {
        return adaugaOrganizator(username, parola, nume, prenume, email, telefon, new String[] { numeOrganizatie });
    }

    public Organizator adaugaOrganizator(String username, String parola, String nume, String prenume, String email, String telefon, String[] numeOrganizatii) {
        validaUsernameLiber(username);
        verificaSpatiuDisponibil();

        Organizator organizator = new Organizator(genereazaId(), username, parola, nume, prenume, email, telefon, numeOrganizatii);
        utilizatori[numarUtilizatori] = organizator;
        numarUtilizatori++;
        adaugaInIndex(organizator);

        save();
        return organizator;
    }

    public Admin adaugaAdmin(String username, String parola, String nume, String prenume, String email, String telefon) {
        validaUsernameLiber(username);
        verificaSpatiuDisponibil();

        Admin admin = new Admin(genereazaId(), username, parola, nume, prenume, email, telefon);
        utilizatori[numarUtilizatori] = admin;
        numarUtilizatori++;
        adaugaInIndex(admin);

        save();
        return admin;
    }

    public AgentCheckIn adaugaAgentCheckIn(String username, String parola, String nume, String prenume, String email, String telefon) {
        validaUsernameLiber(username);
        verificaSpatiuDisponibil();

        AgentCheckIn agent = new AgentCheckIn(genereazaId(), username, parola, nume, prenume, email, telefon);
        utilizatori[numarUtilizatori] = agent;
        numarUtilizatori++;
        adaugaInIndex(agent);

        save();
        return agent;
    }

    public void adaugaOrganizatiePentruOrganizator(int organizatorId, String numeOrganizatie) throws EntitateInexistentaException {
        Utilizator utilizator = findById(organizatorId);

        if (!(utilizator instanceof Organizator)) {
            throw new IllegalArgumentException("Utilizatorul nu este organizator.");
        }

        Organizator organizator = (Organizator) utilizator;
        organizator.adaugaOrganizatie(numeOrganizatie);
        save();
    }

    public void asigneazaAgentLaEveniment(int agentId, int evenimentId) throws EntitateInexistentaException {
        Utilizator utilizator = findById(agentId);

        if (!(utilizator instanceof AgentCheckIn)) {
            throw new IllegalArgumentException("Utilizatorul nu este agent de check-in.");
        }

        AgentCheckIn agent = (AgentCheckIn) utilizator;
        agent.asigneazaEveniment(evenimentId);

        Set<Integer> evenimente = asignariAgentEvenimente.get(agentId);
        if (evenimente == null) {
            evenimente = new HashSet<Integer>();
            asignariAgentEvenimente.put(agentId, evenimente);
        }

        evenimente.add(evenimentId);
        saveAsignariAgentEvenimente();
    }

    public void stergeAsignareAgentLaEveniment(int agentId, int evenimentId) throws EntitateInexistentaException {
        Utilizator utilizator = findById(agentId);

        if (!(utilizator instanceof AgentCheckIn)) {
            throw new IllegalArgumentException("Utilizatorul nu este agent de check-in.");
        }

        Set<Integer> evenimente = asignariAgentEvenimente.get(agentId);
        if (evenimente == null || !evenimente.contains(evenimentId)) {
            throw new EntitateInexistentaException("Nu exista asignarea agentului la evenimentul specificat.");
        }

        evenimente.remove(evenimentId);

        if (evenimente.isEmpty()) {
            asignariAgentEvenimente.remove(agentId);
        }

        sincronizeazaAsignariInAgent(agentId);
        saveAsignariAgentEvenimente();
    }

    public int[] getEvenimenteAsignateAgent(int agentId) throws EntitateInexistentaException {
        Utilizator utilizator = findById(agentId);

        if (!(utilizator instanceof AgentCheckIn)) {
            throw new IllegalArgumentException("Utilizatorul nu este agent de check-in.");
        }

        Set<Integer> evenimente = asignariAgentEvenimente.get(agentId);
        if (evenimente == null || evenimente.isEmpty()) {
            return new int[0];
        }

        int[] rezultat = new int[evenimente.size()];
        int index = 0;

        for (Integer evenimentId : evenimente) {
            rezultat[index] = evenimentId;
            index++;
        }

        return rezultat;
    }

    public void schimbaParola(int utilizatorId, String parolaCurenta, String parolaNoua) throws EntitateInexistentaException {
        Utilizator utilizator = findById(utilizatorId);

        if (parolaCurenta == null || parolaCurenta.trim().length() == 0) {
            throw new IllegalArgumentException("Parola curenta este obligatorie.");
        }

        if (!utilizator.verificaParola(parolaCurenta)) {
            throw new IllegalArgumentException("Parola curenta este incorecta.");
        }

        if (parolaNoua == null || parolaNoua.trim().length() == 0) {
            throw new IllegalArgumentException("Parola noua este obligatorie.");
        }

        if (parolaNoua.equals(parolaCurenta)) {
            throw new IllegalArgumentException("Parola noua trebuie sa fie diferita de parola curenta.");
        }

        utilizator.setParola(parolaNoua);
        save();
    }

    public Utilizator login(String username, String parola) throws AutentificareEsuataException {
        if (username == null || parola == null) {
            throw new AutentificareEsuataException("Username-ul si parola sunt obligatorii.");
        }

        for (int i = 0; i < numarUtilizatori; i++) {
            Utilizator utilizator = utilizatori[i];

            if (utilizator.getUsername().equalsIgnoreCase(username) && utilizator.verificaParola(parola)) {
                return utilizator;
            }
        }

        throw new AutentificareEsuataException("Credentiale invalide pentru utilizatorul " + username + ".");
    }

    public Utilizator cautaDupaUsername(String username) throws EntitateInexistentaException {
        if (username == null) {
            throw new EntitateInexistentaException("Username-ul nu poate fi null.");
        }

        Utilizator utilizator = utilizatoriDupaUsername.get(cheieUsername(username));

        if (utilizator != null) {
            return utilizator;
        }

        throw new EntitateInexistentaException("Nu exista utilizatorul " + username + ".");
    }

    public Utilizator findById(int id) throws EntitateInexistentaException {
        Utilizator utilizator = utilizatoriDupaId.get(id);

        if (utilizator == null) {
            throw new EntitateInexistentaException("Entitatea cu id=" + id + " nu exista.");
        }

        return utilizator;
    }

    public void deleteById(int id) throws EntitateInexistentaException {
        int pozitie = -1;

        for (int i = 0; i < numarUtilizatori; i++) {
            if (utilizatori[i].getId() == id) {
                pozitie = i;
                break;
            }
        }

        if (pozitie == -1) {
            throw new EntitateInexistentaException("Entitatea cu id=" + id + " nu exista.");
        }

        stergeDinIndex(utilizatori[pozitie]);

        if (utilizatori[pozitie] != null && utilizatori[pozitie].getRol() == RolUtilizator.AGENT_CHECK_IN) {
            asignariAgentEvenimente.remove(utilizatori[pozitie].getId());
            saveAsignariAgentEvenimente();
        }

        for (int i = pozitie; i < numarUtilizatori - 1; i++) {
            utilizatori[i] = utilizatori[i + 1];
        }

        utilizatori[numarUtilizatori - 1] = null;
        numarUtilizatori--;

        save();
    }

    public Utilizator[] listAll() {
        Utilizator[] copie = new Utilizator[numarUtilizatori];

        for (int i = 0; i < numarUtilizatori; i++) {
            copie[i] = utilizatori[i];
        }

        return copie;
    }

    private void validaUsernameLiber(String username) {
        if (username == null || username.trim().length() == 0) {
            throw new IllegalArgumentException("Username-ul este obligatoriu.");
        }

        if (utilizatoriDupaUsername.containsKey(cheieUsername(username))) {
            throw new IllegalArgumentException("Exista deja un utilizator cu username-ul " + username + ".");
        }
    }

    private void verificaSpatiuDisponibil() {
        if (numarUtilizatori >= CAPACITATE_MAXIMA) {
            throw new IllegalStateException("S-a atins capacitatea maxima de utilizatori.");
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

                String[] parti = linie.split(";", -1);

                if (parti.length < 8) {
                    continue;
                }

                int id = Integer.parseInt(parti[0]);
                String rolText = parti[1];

                Utilizator utilizator = null;

                if (rolText.equals("CLIENT")) {
                    utilizator = new Client(
                            id,
                            parti[2],
                            parti[3],
                            parti[4],
                            parti[5],
                            parti[6],
                            parti[7]);
                } else if (rolText.equals("ORGANIZATOR")) {
                    if (parti.length >= 9) {
                        String extra = parti[8];
                        if (extra == null || extra.trim().length() == 0) {
                            extra = "Organizatie necunoscuta";
                        }

                        utilizator = new Organizator(
                                id,
                                parti[2],
                                parti[3],
                                parti[4],
                                parti[5],
                                parti[6],
                                parti[7],
                                extra.split(","));
                    }
                } else if (rolText.equals("ADMIN")) {
                    utilizator = new Admin(
                            id,
                            parti[2],
                            parti[3],
                            parti[4],
                            parti[5],
                            parti[6],
                            parti[7]);
                } else if (rolText.equals("AGENT_CHECK_IN")) {
                    utilizator = new AgentCheckIn(
                            id,
                            parti[2],
                            parti[3],
                            parti[4],
                            parti[5],
                            parti[6],
                            parti[7]);
                }

                if (utilizator != null) {
                    utilizatori[numarUtilizatori] = utilizator;
                    numarUtilizatori++;
                    adaugaInIndex(utilizator);

                    if (id >= nextId) {
                        nextId = id + 1;
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Eroare la incarcarea utilizatorilor: " + e.getMessage());
        }
    }

    private void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(NUME_FISIER));

            writer.write("id;rol;username;parola;nume;prenume;email;telefon;extra");
            writer.newLine();

            for (int i = 0; i < numarUtilizatori; i++) {
                Utilizator utilizator = utilizatori[i];
                String extra = "";

                if (utilizator.getRol() == RolUtilizator.ORGANIZATOR) {
                    extra = ((Organizator) utilizator).getOrganizatiiCaText();
                }

                String linie = utilizator.getId() + ";" +
                        utilizator.getRol().name() + ";" +
                        utilizator.getUsername() + ";" +
                        utilizator.getParola() + ";" +
                        utilizator.getNume() + ";" +
                        utilizator.getPrenume() + ";" +
                        utilizator.getEmail() + ";" +
                        utilizator.getTelefon() + ";" +
                        extra;

                writer.write(linie);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Eroare la salvarea utilizatorilor: " + e.getMessage());
        }
    }

    private void loadAsignariAgentEvenimente() {
        File fisier = new File(NUME_FISIER_ASIGNARI_AGENT);

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

                if (primaLinie && linie.startsWith("agentId;")) {
                    primaLinie = false;
                    continue;
                }

                primaLinie = false;

                String[] parti = linie.split(";", -1);
                if (parti.length < 2) {
                    continue;
                }

                int agentId;
                try {
                    agentId = Integer.parseInt(parti[0]);
                } catch (NumberFormatException e) {
                    continue;
                }

                Utilizator utilizator = utilizatoriDupaId.get(agentId);
                if (!(utilizator instanceof AgentCheckIn)) {
                    continue;
                }

                String textEvenimente = parti[1];
                if (textEvenimente == null || textEvenimente.trim().length() == 0) {
                    continue;
                }

                Set<Integer> evenimente = new HashSet<Integer>();
                String[] ids = textEvenimente.split(",");

                for (int i = 0; i < ids.length; i++) {
                    try {
                        int evenimentId = Integer.parseInt(ids[i].trim());
                        evenimente.add(evenimentId);
                    } catch (NumberFormatException e) {
                        // Ignoram id-urile invalide din fisier.
                    }
                }

                if (!evenimente.isEmpty()) {
                    asignariAgentEvenimente.put(agentId, evenimente);
                    sincronizeazaAsignariInAgent(agentId);
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Eroare la incarcarea asignarilor agentilor: " + e.getMessage());
        }
    }

    private void saveAsignariAgentEvenimente() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(NUME_FISIER_ASIGNARI_AGENT));
            writer.write("agentId;evenimente");
            writer.newLine();

            for (Map.Entry<Integer, Set<Integer>> entry : asignariAgentEvenimente.entrySet()) {
                int agentId = entry.getKey();
                Set<Integer> evenimente = entry.getValue();

                if (evenimente == null || evenimente.isEmpty()) {
                    continue;
                }

                String textEvenimente = "";
                int index = 0;

                for (Integer evenimentId : evenimente) {
                    if (index > 0) {
                        textEvenimente = textEvenimente + ",";
                    }
                    textEvenimente = textEvenimente + evenimentId;
                    index++;
                }

                writer.write(agentId + ";" + textEvenimente);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Eroare la salvarea asignarilor agentilor: " + e.getMessage());
        }
    }

    private void sincronizeazaAsignariInAgent(int agentId) {
        Utilizator utilizator = utilizatoriDupaId.get(agentId);

        if (!(utilizator instanceof AgentCheckIn)) {
            return;
        }

        Set<Integer> evenimente = asignariAgentEvenimente.get(agentId);
        if (evenimente == null || evenimente.isEmpty()) {
            ((AgentCheckIn) utilizator).setEvenimenteAsignateDinText("");
            return;
        }

        String textEvenimente = "";
        int index = 0;

        for (Integer evenimentId : evenimente) {
            if (index > 0) {
                textEvenimente = textEvenimente + ",";
            }
            textEvenimente = textEvenimente + evenimentId;
            index++;
        }

        ((AgentCheckIn) utilizator).setEvenimenteAsignateDinText(textEvenimente);
    }
}