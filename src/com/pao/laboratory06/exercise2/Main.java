package com.pao.laboratory06.exercise2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        in.nextLine();

        List<Colaborator> colaboratori = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String linie = in.nextLine().trim();
            while (linie.isEmpty()) {
                linie = in.nextLine().trim();
            }

            Scanner linieScanner = new Scanner(linie);
            String tip = linieScanner.next();

            Colaborator c = switch (tip) {
                case "CIM" -> new CIMColaborator();
                case "PFA" -> new PFAColaborator();
                case "SRL" -> new SRLColaborator();
                default -> throw new IllegalArgumentException("Tip necunoscut: " + tip);
            };

            c.citeste(linieScanner);
            colaboratori.add(c);
            linieScanner.close();
        }

        Comparator<Colaborator> cmpDesc =
                Comparator.comparingDouble(Colaborator::calculeazaVenitNetAnual).reversed();

        // Afișare pe tipuri: CIM, PFA, SRL
        for (TipColaborator tip : TipColaborator.values()) {
            colaboratori.stream()
                    .filter(c -> c.getTip() == tip)
                    .sorted(cmpDesc)
                    .forEach(Colaborator::afiseaza);
        }

        System.out.println();

        Colaborator max = colaboratori.stream()
                .max(Comparator.comparingDouble(Colaborator::calculeazaVenitNetAnual))
                .orElse(null);

        System.out.print("Colaborator cu venit net maxim: ");
        if (max != null) {
            System.out.println(max);
        } else {
            System.out.println("null");
        }

        System.out.println();
        System.out.println("Colaboratori persoane juridice:");

        colaboratori.stream()
                .filter(c -> c instanceof PersoanaJuridica)
                .sorted(cmpDesc)
                .forEach(Colaborator::afiseaza);

        System.out.println();
        System.out.println("Sume și număr colaboratori pe tip:");

        for (TipColaborator tip : TipColaborator.values()) {
            List<Colaborator> listaTip = colaboratori.stream()
                    .filter(c -> c.getTip() == tip)
                    .toList();

            if (listaTip.isEmpty()) {
                System.out.printf("%s: suma = nu lei, număr = null%n", tip);
            } else {
                double suma = listaTip.stream()
                        .mapToDouble(Colaborator::calculeazaVenitNetAnual)
                        .sum();
                int numar = listaTip.size();

                System.out.printf("%s: suma = %.2f lei, număr = %d%n", tip, suma, numar);
            }
        }

        in.close();
    }
}