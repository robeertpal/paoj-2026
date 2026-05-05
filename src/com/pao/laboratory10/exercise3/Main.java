package com.pao.laboratory10.exercise3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Tranzactie> tranzactii = new ArrayList<>();

        tranzactii.add(new Tranzactie(1, 1500.00, "2024-01-10", TipTranzactie.CREDIT, "CONT_A"));
        tranzactii.add(new Tranzactie(2, 350.50, "2024-01-15", TipTranzactie.DEBIT, "CONT_B"));
        tranzactii.add(new Tranzactie(3, 2200.00, "2024-01-20", TipTranzactie.CREDIT, "CONT_A"));

        tranzactii.add(new Tranzactie(4, 120.00, "2024-02-01", TipTranzactie.DEBIT, "CONT_C"));
        tranzactii.add(new Tranzactie(5, 780.75, "2024-02-07", TipTranzactie.CREDIT, "CONT_B"));
        tranzactii.add(new Tranzactie(6, 400.00, "2024-02-12", TipTranzactie.DEBIT, "CONT_A"));

        tranzactii.add(new Tranzactie(7, 3100.00, "2024-03-03", TipTranzactie.CREDIT, "CONT_D"));
        tranzactii.add(new Tranzactie(8, 90.99, "2024-03-08", TipTranzactie.DEBIT, "CONT_C"));
        tranzactii.add(new Tranzactie(9, 650.00, "2024-03-21", TipTranzactie.CREDIT, "CONT_B"));
        tranzactii.add(new Tranzactie(10, 250.25, "2024-03-25", TipTranzactie.DEBIT, "CONT_A"));

        System.out.println("1. Tranzactii CREDIT");
        tranzactii.stream()
                .filter(t -> t.getTip() == TipTranzactie.CREDIT)
                .forEach(System.out::println);

        System.out.println();

        System.out.println("2. Total procesat");
        double totalProcesat = tranzactii.stream()
                .mapToDouble(Tranzactie::getSuma)
                .sum();
        System.out.printf("Total procesat: %.2f RON%n", totalProcesat);

        System.out.println();

        System.out.println("3. Total per luna");
        Map<String, Double> totalPerLuna = tranzactii.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getData().substring(0, 7),
                        TreeMap::new,
                        Collectors.summingDouble(Tranzactie::getSuma)
                ));

        totalPerLuna.forEach((luna, total) ->
                System.out.printf("%s: %.2f RON%n", luna, total)
        );

        System.out.println();

        System.out.println("4. Top 3 tranzactii");
        System.out.println("Top 3 tranzactii:");
        tranzactii.stream()
                .sorted(Comparator.comparingDouble(Tranzactie::getSuma).reversed())
                .limit(3)
                .forEach(System.out::println);

        System.out.println();

        System.out.println("5. Conturi sursa unice");
        List<String> conturiSursaUnice = tranzactii.stream()
                .map(Tranzactie::getContSursa)
                .distinct()
                .collect(Collectors.toList());

        System.out.println("Conturi sursa unice: " + conturiSursaUnice);

        System.out.println();

        System.out.println("6. Suma medie");
        double sumaMedie = tranzactii.stream()
                .mapToDouble(Tranzactie::getSuma)
                .average()
                .orElse(0.0);

        System.out.printf("Suma medie: %.2f RON%n", sumaMedie);

        System.out.println();

        System.out.println("7. Extrase de cont lunare");
        Map<String, List<Tranzactie>> tranzactiiPeLuna = tranzactii.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getData().substring(0, 7),
                        TreeMap::new,
                        Collectors.toList()
                ));

        tranzactiiPeLuna.forEach((luna, listaLuna) -> {
            double totalLuna = listaLuna.stream()
                    .mapToDouble(Tranzactie::getSuma)
                    .sum();

            System.out.printf(
                    "EXTRAS DE CONT - %s: %d tranzactii, total: %.2f RON%n",
                    luna,
                    listaLuna.size(),
                    totalLuna
            );
        });
    }

    private enum TipTranzactie {
        CREDIT,
        DEBIT
    }

    private static class Tranzactie {
        private final int id;
        private final double suma;
        private final String data;
        private final TipTranzactie tip;
        private final String contSursa;

        public Tranzactie(int id, double suma, String data, TipTranzactie tip, String contSursa) {
            this.id = id;
            this.suma = suma;
            this.data = data;
            this.tip = tip;
            this.contSursa = contSursa;
        }

        public int getId() {
            return id;
        }

        public double getSuma() {
            return suma;
        }

        public String getData() {
            return data;
        }

        public TipTranzactie getTip() {
            return tip;
        }

        public String getContSursa() {
            return contSursa;
        }

        @Override
        public String toString() {
            return String.format(
                    "[%d] %s %s: %.2f RON, cont sursa: %s",
                    id,
                    data,
                    tip,
                    suma,
                    contSursa
            );
        }
    }
}