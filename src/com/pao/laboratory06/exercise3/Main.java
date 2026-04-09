package com.pao.laboratory06.exercise3;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== EXERCITIUL 3 - Platforma de plati online ===\n");

        // 1. Creare si sortare array de Inginer
        Inginer[] ingineri = new Inginer[] {
                new Inginer("Popescu", "Andrei", "0711111111", 8000, 2500),
                new Inginer("Ionescu", "Maria", "0722222222", 9500, 4000),
                new Inginer("Georgescu", "Vlad", "0733333333", 7000, 1500)
        };

        System.out.println("Ingineri inainte de sortare:");
        for (Inginer inginer : ingineri) {
            System.out.println(inginer);
        }

        Arrays.sort(ingineri);
        System.out.println("\nIngineri sortati natural (dupa nume):");
        for (Inginer inginer : ingineri) {
            System.out.println(inginer);
        }

        Arrays.sort(ingineri, new ComparatorInginerSalariu());
        System.out.println("\nIngineri sortati descrescator dupa salariu:");
        for (Inginer inginer : ingineri) {
            System.out.println(inginer);
        }

        // 2. Demonstrare acces prin referinta de tip PlataOnline
        System.out.println("\n=== Referinta de tip PlataOnline catre Inginer ===");
        PlataOnline clientOnline = new Inginer("Stan", "Radu", "0744444444", 8500, 3000);

        clientOnline.autentificare("radu.stan", "parola123");
        System.out.println("Sold curent: " + clientOnline.consultareSold());
        System.out.println("Plata 500: " + clientOnline.efectuarePlata(500));
        System.out.println("Sold dupa plata: " + clientOnline.consultareSold());

        // clientOnline.getSalariu(); // NU merge - referinta este de tip PlataOnline

        // 3. Demonstrare acces prin referinta de tip PlataOnlineSMS
        System.out.println("\n=== Referinta de tip PlataOnlineSMS catre PersoanaJuridica ===");
        PlataOnlineSMS firma = new PersoanaJuridica("SC Tech Solutions SRL", "Administrator", "0755555555", 10000);

        firma.autentificare("tech.solutions", "firma2024");
        System.out.println("Sold firma: " + firma.consultareSold());
        System.out.println("Plata 1200: " + firma.efectuarePlata(1200));
        System.out.println("Sold dupa plata: " + firma.consultareSold());

        System.out.println("SMS valid trimis: " + firma.trimiteSMS("Plata a fost procesata cu succes."));
        System.out.println("SMS invalid (gol): " + firma.trimiteSMS(""));
        System.out.println("SMS invalid (null): " + firma.trimiteSMS(null));

        PersoanaJuridica firmaConcreta = (PersoanaJuridica) firma;
        System.out.println("Mesaje SMS salvate: " + firmaConcreta.getSmsTrimise());

        // 4. Caz fara telefon
        System.out.println("\n=== Caz PersoanaJuridica fara telefon ===");
        PersoanaJuridica firmaFaraTelefon = new PersoanaJuridica("SC Fara Telefon SRL", "Manager", "", 5000);
        System.out.println("Trimite SMS fara telefon: " + firmaFaraTelefon.trimiteSMS("Mesaj test"));
        System.out.println("Lista SMS-uri firma fara telefon: " + firmaFaraTelefon.getSmsTrimise());

        // 5. Afisare constanta din enum
        System.out.println("\n=== Constante financiare ===");
        System.out.println("TVA = " + ConstanteFinanciare.TVA.getValoare());
        System.out.println("Salariu minim = " + ConstanteFinanciare.SALARIU_MINIM.getValoare());
        System.out.println("Cota impozit = " + ConstanteFinanciare.COTA_IMPOZIT.getValoare());

        // 6. Tratarea cazurilor de eroare
        System.out.println("\n=== Tratarea erorilor ===");

        try {
            clientOnline.autentificare(null, "parola");
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare autentificare cu user null: " + e.getMessage());
        }

        try {
            clientOnline.autentificare("utilizator", "");
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare autentificare cu parola goala: " + e.getMessage());
        }

        try {
            System.out.println("Plata cu suma negativa: " + clientOnline.efectuarePlata(-100));
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare plata cu suma invalida: " + e.getMessage());
        }

        // 7. Demonstratie entitate fara capabilitate SMS
        System.out.println("\n=== Apel SMS pe entitate fara capabilitate SMS ===");
        PlataOnline doarPlata = new Inginer("Marin", "Elena", "0766666666", 9000, 2000);

        try {
            PlataOnlineSMS smsInexistent = (PlataOnlineSMS) doarPlata;
            smsInexistent.trimiteSMS("Acest mesaj nu ar trebui trimis.");
        } catch (ClassCastException e) {
            System.out.println("Eroare: Inginerul nu are capabilitate SMS si nu poate fi tratat ca PlataOnlineSMS.");
        }

    }
}