package com.pao.laboratory07.exercise2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String TEST = "C";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        List<Comanda> comenzi = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String tip = scanner.next();

            switch (tip) {
                case "STANDARD" -> {
                    int id = scanner.nextInt();
                    String client = scanner.next();
                    double valoare = scanner.nextDouble();
                    comenzi.add(new ComandaStandard(id, client, valoare));
                }
                case "PRECOMANDA" -> {
                    int id = scanner.nextInt();
                    String client = scanner.next();
                    double valoare = scanner.nextDouble();
                    String dataLivrare = scanner.next();
                    comenzi.add(new Precomanda(id, client, valoare, dataLivrare));
                }
                case "ABONAMENT" -> {
                    int id = scanner.nextInt();
                    String client = scanner.next();
                    double valoare = scanner.nextDouble();
                    int nrLuni = scanner.nextInt();
                    comenzi.add(new ComandaAbonament(id, client, valoare, nrLuni));
                }
            }
        }

        switch (TEST) {
            case "A" -> ruleazaParteaA(comenzi);
            case "B" -> ruleazaParteaB(comenzi);
            case "C" -> ruleazaParteaC(comenzi);
            default -> ruleazaParteaA(comenzi);
        }

        scanner.close();
    }

    private static void ruleazaParteaA(List<Comanda> comenzi) {
        for (Comanda comanda : comenzi) {
            comanda.procesare();
        }
    }

    private static void ruleazaParteaB(List<Comanda> comenzi) {
        for (Comanda comanda : comenzi) {
            comanda.proceseaza();
        }

        for (Comanda comanda : comenzi) {
            if (comanda.esteSpeciala()) {
                comanda.afiseaza();
            }
        }
    }

    private static void ruleazaParteaC(List<Comanda> comenzi) {
        List<Comanda> faraAbonamente = new ArrayList<>();
        List<Comanda> abonamente = new ArrayList<>();

        for (Comanda comanda : comenzi) {
            if (comanda instanceof ComandaAbonament) {
                abonamente.add(comanda);
            } else {
                faraAbonamente.add(comanda);
            }
        }

        faraAbonamente.sort(Comparator.comparingDouble(Comanda::getValoare).reversed());
        abonamente.sort(Comparator.comparingDouble(Comanda::getValoare));

        List<Comanda> sortate = new ArrayList<>();
        sortate.addAll(faraAbonamente);
        sortate.addAll(abonamente);

        for (Comanda comanda : sortate) {
            comanda.afiseaza();
        }

        Comanda maxim = null;
        for (Comanda comanda : comenzi) {
            if (maxim == null || comanda.getValoare() > maxim.getValoare()) {
                maxim = comanda;
            }
        }

        System.out.println();
        System.out.println("Comanda cu valoarea maximă: " + maxim);
        System.out.println();

        double sumaStandard = 0.0;
        double sumaPrecomanda = 0.0;
        double sumaAbonament = 0.0;

        int nrStandard = 0;
        int nrPrecomanda = 0;
        int nrAbonament = 0;

        for (Comanda comanda : comenzi) {
            if (comanda instanceof ComandaStandard) {
                sumaStandard += comanda.getValoare();
                nrStandard++;
            } else if (comanda instanceof Precomanda) {
                sumaPrecomanda += comanda.getValoare();
                nrPrecomanda++;
            } else if (comanda instanceof ComandaAbonament) {
                sumaAbonament += comanda.getValoare();
                nrAbonament++;
            }
        }

        System.out.println("Sume și număr comenzi pe tip:");
        System.out.printf("STANDARD: suma = %.2f lei, număr = %d%n", sumaStandard, nrStandard);
        System.out.printf("PRECOMANDA: suma = %.2f lei, număr = %d%n", sumaPrecomanda, nrPrecomanda);
        System.out.printf("ABONAMENT: suma = %.2f lei, număr = %d%n", sumaAbonament, nrAbonament);
    }
}