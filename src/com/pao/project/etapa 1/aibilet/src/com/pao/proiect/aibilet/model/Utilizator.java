package com.pao.proiect.aibilet.model;

public abstract class Utilizator {
    protected int id;
    protected String username;
    protected String parola;
    protected String nume;
    protected String prenume;
    protected String email;
    protected String telefon;

    protected Utilizator(int id, String username, String parola, String nume, String prenume, String email, String telefon) {
        if (username == null) {
            throw new IllegalArgumentException("Username-ul nu poate fi null.");
        }
        if (parola == null) {
            throw new IllegalArgumentException("Parola nu poate fi null.");
        }
        if (nume == null) {
            throw new IllegalArgumentException("Numele nu poate fi null.");
        }
        if (prenume == null) {
            throw new IllegalArgumentException("Prenume nu poate fi null.");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email-ul nu poate fi null.");
        }
        if (telefon == null) {
            throw new IllegalArgumentException("Numarul de telefon nu poate fi null.");
        }

        this.id = id;
        this.username = username;
        this.parola = parola;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.telefon = telefon;
    }

    public abstract RolUtilizator getRol();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean verificaParola(String parola) {
        return this.parola.equals(parola);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username-ul nu poate fi null.");
        }
        this.username = username;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        if (parola == null) {
            throw new IllegalArgumentException("Parola nu poate fi null.");
        }
        this.parola = parola;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        if (nume == null) {
            throw new IllegalArgumentException("Numele nu poate fi null.");
        }
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        if (prenume == null) {
            throw new IllegalArgumentException("Prenumele nu poate fi null");
        }
        this.prenume = prenume;
    }

    public String getNumeComplet() {
        return nume + " " + prenume;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email-ul nu poate fi null.");
        }
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        if (telefon == null) {
            throw new IllegalArgumentException("Numarul de telefon nu poate fi null.");
        }
        this.telefon = telefon;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "\n" +
                "   ID: " + id + "\n" +
                "   Username: " + username + "\n" +
                "   Nume: " + nume + "\n" +
                "   Prenume: " + prenume + "\n" +
                "   Email: " + email + "\n" +
                "   Telefon: " + telefon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Utilizator)) {
            return false;
        }

        Utilizator utilizator = (Utilizator) o;

        return username.equals(utilizator.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}