package com.pao.laboratory06.exercise3;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== EXERCITIUL 3 - DEMONSTRATIE ===");
        System.out.println();

        // 1. Cream un array de ingineri
        Inginer[] ingineri = {
                new Inginer("Popescu", "Ana", "0711111111", 9000, "ana", "pass1", 5000),
                new Inginer("Ionescu", "Vlad", "0722222222", 12000, "vlad", "pass2", 7000),
                new Inginer("Georgescu", "Maria", "0733333333", 10000, "maria", "pass3", 6000)
        };

        System.out.println("Ingineri - ordinea initiala:");
        for (Inginer inginer : ingineri) {
            System.out.println(inginer);
        }
        System.out.println();

        // 2. Sortare naturala: dupa nume
        Arrays.sort(ingineri);
        System.out.println("Ingineri sortati natural (dupa nume):");
        for (Inginer inginer : ingineri) {
            System.out.println(inginer);
        }
        System.out.println();

        // 3. Sortare alternativa: dupa salariu descrescator
        Arrays.sort(ingineri, new ComparatorInginerSalariu());
        System.out.println("Ingineri sortati dupa salariu descrescator:");
        for (Inginer inginer : ingineri) {
            System.out.println(inginer);
        }
        System.out.println();

        // 4. Acces prin referinta de tip interfață PlataOnline
        PlataOnline clientOnline = new Inginer("Dumitrescu", "Paul", "0744444444",
                11000, "paul", "secret", 4000);

        System.out.println("Demonstratie CAN_DO prin referinta PlataOnline:");
        clientOnline.autentificare("paul", "secret");
        System.out.println("Sold curent: " + clientOnline.consultareSold());
        System.out.println("Plata 1500 reusita? " + clientOnline.efectuarePlata(1500));
        System.out.println("Sold dupa plata: " + clientOnline.consultareSold());
        System.out.println();

        // 5. PersoanaJuridica prin referinta PlataOnlineSMS
        PlataOnlineSMS firma = new PersoanaJuridica("Firma", "Alpha", "0755555555",
                "firma", "firma123", 20000);

        System.out.println("Demonstratie referinta PlataOnlineSMS:");
        firma.autentificare("firma", "firma123");
        System.out.println("Sold firma: " + firma.consultareSold());
        System.out.println("SMS trimis? " + firma.trimiteSMS("Plata a fost efectuata."));
        System.out.println("Plata 5000 reusita? " + firma.efectuarePlata(5000));
        System.out.println("Sold firma dupa plata: " + firma.consultareSold());
        System.out.println();

        // 6. Stocarea mesajelor SMS
        PersoanaJuridica firmaConcreta = new PersoanaJuridica("Companie", "Beta", "0766666666",
                "beta", "beta123", 15000);
        firmaConcreta.trimiteSMS("Mesaj 1");
        firmaConcreta.trimiteSMS("Mesaj 2");

        System.out.println("SMS-uri stocate pentru firmaConcreta:");
        for (String sms : firmaConcreta.getSmsTrimise()) {
            System.out.println(sms);
        }
        System.out.println();

        // 7. Caz fara telefon
        PersoanaJuridica faraTelefon = new PersoanaJuridica("Companie", "Gamma", "",
                "gamma", "gamma123", 10000);
        System.out.println("Trimitere SMS fara telefon: " + faraTelefon.trimiteSMS("Test SMS"));
        System.out.println();

        // 8. Caz cu mesaj invalid
        System.out.println("Trimitere SMS gol: " + firmaConcreta.trimiteSMS(""));
        System.out.println();

        // 9. Afisare constanta din enum
        System.out.println("Constanta financiara TVA = " + ConstanteFinanciare.TVA.getValoare());
        System.out.println("Constanta financiara SALARIU_MINIM = " + ConstanteFinanciare.SALARIU_MINIM.getValoare());
        System.out.println();

        // 10. Tratare erori: autentificare cu user null
        try {
            clientOnline.autentificare(null, "abc");
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare autenificare: " + e.getMessage());
        }
        System.out.println();

        // 11. Tratare erori: plata cu suma invalida
        try {
            clientOnline.efectuarePlata(-50);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare plata: " + e.getMessage());
        }
        System.out.println();

        // 12. Demonstratie apel SMS pe entitate fara capabilitate SMS
        try {
            PlataOnline doarPlata = new Inginer("Marin", "Ioan", "0777777777",
                    9500, "ioan", "pass", 3000);

            if (!(doarPlata instanceof PlataOnlineSMS)) {
                throw new UnsupportedOperationException("Entittea nu are capabilitate SMS.");
            }

            ((PlataOnlineSMS) doarPlata).trimiteSMS("Mesaj impsibil");
        } catch (UnsupportedOperationException e) {
            System.out.println("Eroare SMS: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== SFARSIT DEMONSTRATIE ===");
    }
}