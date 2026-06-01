package com.pao.proiect.aibilet.model;

public class AgentCheckIn extends Agent {
    private int[] evenimenteAsignate;
    private int numarEvenimenteAsignate;

    public AgentCheckIn(int id, String username, String parola, String nume, String prenume, String email, String telefon) {
        super(id, username, parola, nume, prenume, email, telefon);
        this.evenimenteAsignate = new int[100];
        this.numarEvenimenteAsignate = 0;
    }

    public void asigneazaEveniment(int evenimentId) {
        if (evenimentId <= 0) {
            throw new IllegalArgumentException("Id-ul evenimentului trebuie sa fie pozitiv.");
        }

        if (esteAsignatLaEveniment(evenimentId)) {
            return;
        }

        if (numarEvenimenteAsignate == evenimenteAsignate.length) {
            throw new IllegalStateException("Agentul nu mai poate fi asignat la alte evenimente.");
        }

        evenimenteAsignate[numarEvenimenteAsignate] = evenimentId;
        numarEvenimenteAsignate++;
    }

    public boolean esteAsignatLaEveniment(int evenimentId) {
        for (int i = 0; i < numarEvenimenteAsignate; i++) {
            if (evenimenteAsignate[i] == evenimentId) {
                return true;
            }
        }

        return false;
    }

    public int[] getEvenimenteAsignate() {
        int[] copie = new int[numarEvenimenteAsignate];

        for (int i = 0; i < numarEvenimenteAsignate; i++) {
            copie[i] = evenimenteAsignate[i];
        }

        return copie;
    }

    public int getNumarEvenimenteAsignate() {
        return numarEvenimenteAsignate;
    }

    protected String evenimenteAsignateCaText() {
        String rezultat = "";

        for (int i = 0; i < numarEvenimenteAsignate; i++) {
            if (i > 0) {
                rezultat += ",";
            }

            rezultat += evenimenteAsignate[i];
        }

        return rezultat;
    }

    protected void incarcaEvenimenteAsignateDinText(String text) {
        this.numarEvenimenteAsignate = 0;

        if (text == null || text.trim().equals("")) {
            return;
        }

        String[] parti = text.split(",");

        for (int i = 0; i < parti.length; i++) {
            try {
                int evenimentId = Integer.parseInt(parti[i].trim());
                asigneazaEveniment(evenimentId);
            } catch (NumberFormatException e) {
                // ignoram id-urile invalide din fisier
            }
        }
    }

    public String getEvenimenteAsignateCaText() {
        return evenimenteAsignateCaText();
    }

    public void setEvenimenteAsignateDinText(String text) {
        incarcaEvenimenteAsignateDinText(text);
    }

    @Override
    public RolUtilizator getRol() {
        return RolUtilizator.AGENT_CHECK_IN;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "\n" +
                "   ID: " + id + "\n" +
                "   Username: " + username + "\n" +
                "   Nume: " + nume + "\n" +
                "   Prenume: " + prenume + "\n" +
                "   Email: " + email + "\n" +
                "   Telefon: " + telefon + "\n" +
                "   Evenimente asignate: " + getEvenimenteAsignateCaText();
    }
}