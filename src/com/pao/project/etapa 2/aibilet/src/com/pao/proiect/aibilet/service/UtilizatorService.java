package com.pao.proiect.aibilet.service;

import com.pao.proiect.aibilet.exception.AutentificareEsuataException;
import com.pao.proiect.aibilet.exception.EntitateInexistentaException;
import com.pao.proiect.aibilet.model.Admin;
import com.pao.proiect.aibilet.model.AgentCheckIn;
import com.pao.proiect.aibilet.model.Client;
import com.pao.proiect.aibilet.model.Organizator;
import com.pao.proiect.aibilet.model.RolUtilizator;
import com.pao.proiect.aibilet.model.Utilizator;
import com.pao.proiect.aibilet.repository.AgentEventAssignmentRepository;
import com.pao.proiect.aibilet.repository.AgentEventAssignmentRepository.AgentEventAssignment;
import com.pao.proiect.aibilet.repository.UtilizatorRepository;

import java.util.List;
import java.util.Optional;

public class UtilizatorService {
    private static final UtilizatorService INSTANCE = new UtilizatorService();

    private final UtilizatorRepository utilizatorRepository;
    private final AgentEventAssignmentRepository agentEventAssignmentRepository;

    private UtilizatorService() {
        this.utilizatorRepository = UtilizatorRepository.getInstance();
        this.agentEventAssignmentRepository = AgentEventAssignmentRepository.getInstance();
    }

    public static UtilizatorService getInstance() {
        return INSTANCE;
    }

    private String cheieUsername(String username) {
        if (username == null) {
            return "";
        }
        return username.trim().toLowerCase();
    }

    public Client adaugaClient(String username, String parola, String nume, String prenume, String email, String telefon) {
        validaUsernameLiber(username);

        Client client = new Client(0, username, parola, nume, prenume, email, telefon);
        return (Client) utilizatorRepository.create(client);
    }

    public Organizator adaugaOrganizator(String username, String parola, String nume, String prenume, String email, String telefon, String numeOrganizatie) {
        return adaugaOrganizator(username, parola, nume, prenume, email, telefon, new String[] { numeOrganizatie });
    }

    public Organizator adaugaOrganizator(String username, String parola, String nume, String prenume, String email, String telefon, String[] numeOrganizatii) {
        validaUsernameLiber(username);

        Organizator organizator = new Organizator(0, username, parola, nume, prenume, email, telefon, numeOrganizatii);
        return (Organizator) utilizatorRepository.create(organizator);
    }

    public Admin adaugaAdmin(String username, String parola, String nume, String prenume, String email, String telefon) {
        validaUsernameLiber(username);

        Admin admin = new Admin(0, username, parola, nume, prenume, email, telefon);
        return (Admin) utilizatorRepository.create(admin);
    }

    public AgentCheckIn adaugaAgentCheckIn(String username, String parola, String nume, String prenume, String email, String telefon) {
        validaUsernameLiber(username);

        AgentCheckIn agent = new AgentCheckIn(0, username, parola, nume, prenume, email, telefon);
        return (AgentCheckIn) utilizatorRepository.create(agent);
    }

    public void adaugaOrganizatiePentruOrganizator(int organizatorId, String numeOrganizatie) throws EntitateInexistentaException {
        Utilizator utilizator = findById(organizatorId);

        if (!(utilizator instanceof Organizator)) {
            throw new IllegalArgumentException("Utilizatorul nu este organizator.");
        }

        Organizator organizator = (Organizator) utilizator;
        organizator.adaugaOrganizatie(numeOrganizatie);
        utilizatorRepository.update(organizator);
    }

    public void asigneazaAgentLaEveniment(int agentId, int evenimentId) throws EntitateInexistentaException {
        Utilizator utilizator = findById(agentId);

        if (!(utilizator instanceof AgentCheckIn)) {
            throw new IllegalArgumentException("Utilizatorul nu este agent de check-in.");
        }

        try {
            agentEventAssignmentRepository.createAssignment(agentId, evenimentId);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void stergeAsignareAgentLaEveniment(int agentId, int evenimentId) throws EntitateInexistentaException {
        Utilizator utilizator = findById(agentId);

        if (!(utilizator instanceof AgentCheckIn)) {
            throw new IllegalArgumentException("Utilizatorul nu este agent de check-in.");
        }

        boolean deleted = agentEventAssignmentRepository.deleteAssignment(agentId, evenimentId);

        if (!deleted) {
            throw new EntitateInexistentaException("Nu exista asignarea agentului la evenimentul specificat.");
        }
    }

    public int[] getEvenimenteAsignateAgent(int agentId) throws EntitateInexistentaException {
        Utilizator utilizator = findById(agentId);

        if (!(utilizator instanceof AgentCheckIn)) {
            throw new IllegalArgumentException("Utilizatorul nu este agent de check-in.");
        }

        List<AgentEventAssignment> asignari = agentEventAssignmentRepository.findByAgentId(agentId);
        int[] rezultat = new int[asignari.size()];

        for (int i = 0; i < asignari.size(); i++) {
            rezultat[i] = asignari.get(i).getEvenimentId();
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
        utilizatorRepository.update(utilizator);
    }

    public Utilizator login(String username, String parola) throws AutentificareEsuataException {
        if (username == null || parola == null) {
            throw new AutentificareEsuataException("Username-ul si parola sunt obligatorii.");
        }

        Utilizator[] toti = listAll();
        for (int i = 0; i < toti.length; i++) {
            Utilizator utilizator = toti[i];
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

        Utilizator[] toti = listAll();
        String cheie = cheieUsername(username);

        for (int i = 0; i < toti.length; i++) {
            if (cheieUsername(toti[i].getUsername()).equals(cheie)) {
                return toti[i];
            }
        }

        throw new EntitateInexistentaException("Nu exista utilizatorul " + username + ".");
    }

    public Utilizator findById(int id) throws EntitateInexistentaException {
        Optional<Utilizator> utilizator = utilizatorRepository.findById(id);

        if (utilizator.isPresent()) {
            return hidratazaAsignariAgent(utilizator.get());
        }

        throw new EntitateInexistentaException("Entitatea cu id=" + id + " nu exista.");
    }

    public void delete(int id) throws EntitateInexistentaException {
        Utilizator utilizator = findById(id);

        if (utilizator.getRol() == RolUtilizator.AGENT_CHECK_IN) {
            List<AgentEventAssignment> asignari = agentEventAssignmentRepository.findByAgentId(id);
            for (int i = 0; i < asignari.size(); i++) {
                agentEventAssignmentRepository.deleteAssignment(id, asignari.get(i).getEvenimentId());
            }
        }

        utilizatorRepository.delete(id);
    }

    public Utilizator[] listAll() {
        List<Utilizator> utilizatoriDinBaza = utilizatorRepository.findAll();
        Utilizator[] copie = new Utilizator[utilizatoriDinBaza.size()];

        for (int i = 0; i < utilizatoriDinBaza.size(); i++) {
            copie[i] = hidratazaAsignariAgent(utilizatoriDinBaza.get(i));
        }

        return copie;
    }

    private void validaUsernameLiber(String username) {
        if (username == null || username.trim().length() == 0) {
            throw new IllegalArgumentException("Username-ul este obligatoriu.");
        }

        try {
            cautaDupaUsername(username);
            throw new IllegalArgumentException("Exista deja un utilizator cu username-ul " + username + ".");
        } catch (EntitateInexistentaException e) {
            // Username disponibil.
        }
    }

    private Utilizator hidratazaAsignariAgent(Utilizator utilizator) {
        if (utilizator instanceof AgentCheckIn) {
            List<AgentEventAssignment> asignari = agentEventAssignmentRepository.findByAgentId(utilizator.getId());
            String textEvenimente = "";

            for (int i = 0; i < asignari.size(); i++) {
                if (i > 0) {
                    textEvenimente = textEvenimente + ",";
                }
                textEvenimente = textEvenimente + asignari.get(i).getEvenimentId();
            }

            ((AgentCheckIn) utilizator).setEvenimenteAsignateDinText(textEvenimente);
        }

        return utilizator;
    }

}
