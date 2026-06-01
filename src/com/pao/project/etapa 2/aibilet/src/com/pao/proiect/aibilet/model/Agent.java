package com.pao.proiect.aibilet.model;

public abstract class Agent extends Utilizator {
    protected Agent(int id, String username, String parola, String nume, String prenume, String email, String telefon) {
        super(id, username, parola, nume, prenume, email, telefon);
    }
}