package com.pao.laboratory05.angajati;

import java.util.Arrays;

public class AngajatService {
    private Angajat[] angajati = new Angajat[0];

    private AngajatService() {
    }

    private static class Holder {
        private static final AngajatService INSTANCE = new AngajatService();
    }

    public static AngajatService getInstance() {
        return Holder.INSTANCE;
    }

    public void addAngajat(Angajat a) {
        Angajat[] copie = new Angajat[angajati.length + 1];
        System.arraycopy(angajati, 0, copie, 0, angajati.length);
        copie[copie.length - 1] = a;
        angajati = copie;

        System.out.println("Angajat adăugat: " + a.getNume());
    }

    public void printAll() {
        for (Angajat angajat : angajati) {
            System.out.println(angajat);
        }
    }

    public void listBySalary() {
        Angajat[] copy = angajati.clone();
        Arrays.sort(copy);

        System.out.println("--- Angajați după salariu (descrescător) ---");
        for (int i = 0; i < copy.length; i++) {
            System.out.println((i + 1) + ". " + copy[i]);
        }
    }

    public void findByDepartament(String numeDept) {
        boolean gasit = false;

        System.out.println("--- Angajați din " + numeDept + " ---");
        for (Angajat angajat : angajati) {
            if (angajat.getDepartament().nume().equalsIgnoreCase(numeDept)) {
                System.out.println(angajat);
                gasit = true;
            }
        }

        if (!gasit) {
            System.out.println("Niciun angajat în departamentul: " + numeDept);
        }
    }
}