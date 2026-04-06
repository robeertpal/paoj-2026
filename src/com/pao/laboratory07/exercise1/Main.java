package com.pao.laboratory07.exercise1;

import java.util.Scanner;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Stack<StareComanda> istoric = new Stack<>();

        StareComanda stareCurenta = StareComanda.valueOf(scanner.nextLine());
        boolean mesajFinalInAsteptare = false;

        System.out.println(stareCurenta);

        while (scanner.hasNextLine()) {
            String comanda = scanner.nextLine();

            if (comanda.equals("QUIT")) {
                if (mesajFinalInAsteptare) {
                    System.out.println("Comanda este in stare finala.");
                }
                break;
            }

            if (comanda.equals("undo")) {
                if (!istoric.isEmpty()) {
                    stareCurenta = istoric.pop();
                }
                mesajFinalInAsteptare = false;
                System.out.println(stareCurenta);
                continue;
            }

            if (esteFinala(stareCurenta)) {
                System.out.println("Comanda este in stare finala.");
                mesajFinalInAsteptare = false;
                continue;
            }

            if (comanda.equals("next")) {
                istoric.push(stareCurenta);
                stareCurenta = urmatoareaStare(stareCurenta);
                System.out.println(stareCurenta);
                mesajFinalInAsteptare = esteFinala(stareCurenta);
            } else if (comanda.equals("cancel")) {
                istoric.push(stareCurenta);
                stareCurenta = StareComanda.ANULATA;
                System.out.println(stareCurenta);
                mesajFinalInAsteptare = true;
            } else {
                System.out.println(stareCurenta);
                mesajFinalInAsteptare = false;
            }
        }

        scanner.close();
    }

    private static boolean esteFinala(StareComanda stare) {
        return stare == StareComanda.LIVRATA || stare == StareComanda.ANULATA;
    }

    private static StareComanda urmatoareaStare(StareComanda stare) {
        switch (stare) {
            case PLASATA:
                return StareComanda.PROCESATA;
            case PROCESATA:
                return StareComanda.EXPEDIATA;
            case EXPEDIATA:
                return StareComanda.LIVRATA;
            default:
                return stare;
        }
    }
}