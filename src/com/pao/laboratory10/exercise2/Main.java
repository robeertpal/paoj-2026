package com.pao.laboratory10.exercise2;

import com.pao.laboratory10.exercise1.TipTranzactie;
import com.pao.laboratory10.exercise1.Tranzactie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        ArrayList<Tranzactie> lista = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        if (!scanner.hasNextInt()) {
            scanner.close();
            return;
        }

        int n = scanner.nextInt();

        for (int i = 0; i < n; i++) {
            int id = scanner.nextInt();
            double suma = Double.parseDouble(scanner.next());
            String data = scanner.next();
            TipTranzactie tip = TipTranzactie.valueOf(scanner.next());

            lista.add(new Tranzactie(id, suma, data, tip));
        }

        while (scanner.hasNext()) {
            String comanda = scanner.next();

            switch (comanda) {
                case "UNIQUE_IDS": {
                    LinkedHashSet<Integer> idsUnice = new LinkedHashSet<>();

                    for (Tranzactie tranzactie : lista) {
                        idsUnice.add(tranzactie.getId());
                    }

                    System.out.println("IDs unice (" + idsUnice.size() + "): " + idsUnice);
                    break;
                }

                case "MONTHLY_REPORT": {
                    TreeMap<String, double[]> raport = new TreeMap<>();

                    for (Tranzactie tranzactie : lista) {
                        String luna = tranzactie.getData().substring(0, 7);

                        raport.putIfAbsent(luna, new double[2]);

                        if (tranzactie.getTip() == TipTranzactie.CREDIT) {
                            raport.get(luna)[0] += tranzactie.getSuma();
                        } else {
                            raport.get(luna)[1] += tranzactie.getSuma();
                        }
                    }

                    for (String luna : raport.keySet()) {
                        double credit = raport.get(luna)[0];
                        double debit = raport.get(luna)[1];

                        System.out.printf(
                                "%s: CREDIT %.2f RON, DEBIT %.2f RON%n",
                                luna,
                                credit,
                                debit
                        );
                    }

                    break;
                }

                case "TOP": {
                    int topN = scanner.nextInt();

                    ArrayList<Tranzactie> copie = new ArrayList<>(lista);
                    copie.sort(Comparator.comparingDouble(Tranzactie::getSuma).reversed());

                    int limita = Math.min(topN, copie.size());

                    System.out.println("Top " + topN + ":");
                    for (int i = 0; i < limita; i++) {
                        System.out.println(copie.get(i));
                    }

                    break;
                }

                case "SORT_ASC": {
                    lista.sort(Comparator.comparingDouble(Tranzactie::getSuma));
                    afiseazaLista(lista);
                    break;
                }

                case "SORT_DESC": {
                    lista.sort(Comparator.comparingDouble(Tranzactie::getSuma).reversed());
                    afiseazaLista(lista);
                    break;
                }

                case "REVERSE": {
                    Collections.reverse(lista);
                    afiseazaLista(lista);
                    break;
                }

                case "MIN_MAX": {
                    Comparator<Tranzactie> comparatorSuma =
                            Comparator.comparingDouble(Tranzactie::getSuma);

                    Tranzactie minim = Collections.min(lista, comparatorSuma);
                    Tranzactie maxim = Collections.max(lista, comparatorSuma);

                    System.out.println("MIN: " + minim);
                    System.out.println("MAX: " + maxim);
                    break;
                }

                case "CME_DEMO": {
                    try {
                        for (Tranzactie tranzactie : lista) {
                            lista.remove(tranzactie);
                        }
                    } catch (ConcurrentModificationException e) {
                        System.out.println(
                                "ConcurrentModificationException prins: modificare in iteratie detectata."
                        );
                    }

                    break;
                }

                default:
                    break;
            }
        }

        scanner.close();
    }

    private static void afiseazaLista(ArrayList<Tranzactie> lista) {
        for (Tranzactie tranzactie : lista) {
            System.out.println(tranzactie);
        }
    }
}