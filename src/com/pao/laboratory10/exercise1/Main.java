package com.pao.laboratory10.exercise1;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LinkedList<Tranzactie> coada = new LinkedList<>();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String comanda = scanner.next();

            switch (comanda) {
                case "ENQUEUE": {
                    int id = scanner.nextInt();
                    double suma = Double.parseDouble(scanner.next());
                    String data = scanner.next();
                    TipTranzactie tip = TipTranzactie.valueOf(scanner.next());

                    Tranzactie tranzactie = new Tranzactie(id, suma, data, tip);
                    coada.addLast(tranzactie);
                    break;
                }

                case "DEQUEUE": {
                    if (coada.isEmpty()) {
                        System.out.println("Coada goala.");
                    } else {
                        Tranzactie tranzactie = coada.removeFirst();
                        System.out.println("Procesat: " + tranzactie);
                    }
                    break;
                }

                case "PUSH": {
                    int id = scanner.nextInt();
                    double suma = Double.parseDouble(scanner.next());
                    String data = scanner.next();
                    TipTranzactie tip = TipTranzactie.valueOf(scanner.next());

                    Tranzactie tranzactie = new Tranzactie(id, suma, data, tip);
                    coada.addFirst(tranzactie);
                    break;
                }

                case "POP": {
                    if (coada.isEmpty()) {
                        System.out.println("Coada goala.");
                    } else {
                        Tranzactie tranzactie = coada.removeFirst();
                        System.out.println("Extras: " + tranzactie);
                    }
                    break;
                }

                case "REMOVE_DEBIT": {
                    int eliminate = 0;

                    Iterator<Tranzactie> iterator = coada.iterator();
                    while (iterator.hasNext()) {
                        Tranzactie tranzactie = iterator.next();

                        if (tranzactie.getTip() == TipTranzactie.DEBIT) {
                            iterator.remove();
                            eliminate++;
                        }
                    }

                    System.out.println("Eliminat " + eliminate + " tranzactii DEBIT.");
                    break;
                }

                case "REMOVE_BELOW": {
                    double threshold = Double.parseDouble(scanner.next());
                    int eliminate = 0;

                    Iterator<Tranzactie> iterator = coada.iterator();
                    while (iterator.hasNext()) {
                        Tranzactie tranzactie = iterator.next();

                        if (tranzactie.getSuma() < threshold) {
                            iterator.remove();
                            eliminate++;
                        }
                    }

                    System.out.printf("Eliminat %d tranzactii sub %.2f RON.%n", eliminate, threshold);
                    break;
                }

                case "PRINT": {
                    for (Tranzactie tranzactie : coada) {
                        System.out.println(tranzactie);
                    }
                    break;
                }

                case "SIZE": {
                    System.out.println("Dimensiune coada: " + coada.size());
                    break;
                }

                default:
                    break;
            }
        }

        scanner.close();
    }
}