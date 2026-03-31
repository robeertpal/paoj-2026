package com.pao.laboratory06.exercise2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim());

        List<Colaborator> colaboratori = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String linie = scanner.nextLine().trim();
            String[] p = linie.split("\\s+");

            String tip = p[0];

            switch (tip) {
                case "CIM" -> {
                    String nume = p[1];
                    String prenume = p[2];
                    double salariu = Double.parseDouble(p[3]);
                    boolean bonus = p.length >= 5 && p[4].equals("DA");
                    colaboratori.add(new CIMColaborator(nume, prenume, salariu, bonus));
                }
                case "PFA" -> {
                    String nume = p[1];
                    String prenume = p[2];
                    double venit = Double.parseDouble(p[3]);
                    double cheltuieli = Double.parseDouble(p[4]);
                    colaboratori.add(new PFAColaborator(nume, prenume, venit, cheltuieli));
                }
                case "SRL" -> {
                    String nume = p[1];
                    String prenume = p[2];
                    double venit = Double.parseDouble(p[3]);
                    double cheltuieli = Double.parseDouble(p[4]);
                    colaboratori.add(new SRLColaborator(nume, prenume, venit, cheltuieli));
                }
            }
        }

        colaboratori.sort(Comparator.comparingDouble(Colaborator::calculeazaVenitNetAnual).reversed());

        for (Colaborator c : colaboratori) {
            System.out.println(c);
        }

        System.out.println();

        Colaborator maxim = colaboratori.stream()
                .max(Comparator.comparingDouble(Colaborator::calculeazaVenitNetAnual))
                .orElse(null);

        if (maxim != null) {
            System.out.println("Colaborator cu venit net maxim: " + maxim);
        }

        System.out.println();
        System.out.println("Colaboratori persoane juridice:");
        for (Colaborator c : colaboratori) {
            if (c instanceof PersoanaJuridica) {
                System.out.println(c);
            }
        }

        System.out.println();
        System.out.println("Sume și număr colaboratori pe tip:");

        Map<TipColaborator, Double> sume = new EnumMap<>(TipColaborator.class);
        Map<TipColaborator, Integer> numere = new EnumMap<>(TipColaborator.class);

        for (TipColaborator tip : TipColaborator.values()) {
            sume.put(tip, 0.0);
            numere.put(tip, 0);
        }

        for (Colaborator c : colaboratori) {
            TipColaborator tip = c.getTip();
            sume.put(tip, sume.get(tip) + c.calculeazaVenitNetAnual());
            numere.put(tip, numere.get(tip) + 1);
        }

        for (TipColaborator tip : TipColaborator.values()) {
            System.out.printf("%s: suma = %.2f lei, număr = %d%n",
                    tip, sume.get(tip), numere.get(tip));
        }
    }
}