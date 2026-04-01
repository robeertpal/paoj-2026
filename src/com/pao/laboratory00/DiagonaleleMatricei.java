package com.pao.laboratory00;

import java.util.Scanner;

/**
 * Exercitiul 2
 *
 * Cititi de la tastatura o matrice de n ori n elemente REALE.
 *
 * 1. Afisati matricea in consola.
 * 2. Afisati suma elementelor de pe diagonala principala
 *    si produsul elementelor de pe diagonala secundara.
 *
 */

public class DiagonaleleMatricei {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        double[][] mat = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mat[i][j] = scanner.nextDouble();
            }
        }

        System.out.println("Matricea este:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println();
        }

        double sumaDiagonalaPrincipala = 0;
        double produsDiagonalaSecundara = 1;

        for (int i = 0; i < n; i++) {
            sumaDiagonalaPrincipala += mat[i][i];
            produsDiagonalaSecundara *= mat[i][n - 1 - i];
        }

        System.out.println("Suma diagonalei principale este: " + sumaDiagonalaPrincipala);
        System.out.println("Produsul diagonalei secundare este: " + produsDiagonalaSecundara);

        scanner.close();
    }
}