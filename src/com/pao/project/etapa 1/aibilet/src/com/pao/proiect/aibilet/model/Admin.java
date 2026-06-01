package com.pao.proiect.aibilet.model;

public class Admin extends Utilizator {

    public Admin(int id, String username, String parola, String nume, String prenume, String email, String telefon) {
        super(id, username, parola, nume, prenume, email, telefon);
    }

    @Override
    public RolUtilizator getRol() {
        return RolUtilizator.ADMIN;
    }
}