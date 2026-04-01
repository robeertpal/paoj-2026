package com.pao.laboratory00;

import java.util.Scanner;

/**
 * Exercitiul 1
 *
 * Cititi de la tastatura un sir cu n elemente intregi.
 *
 * 1. Afisati elementele sirului in doua modalitati.
 * 2. Afisati media aritmetica a elementelor sirului.
 *
 */

public class MediaAritmetica {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        int[] array = new int[n];
        int suma = 0;

        for (int i = 0; i < n; i++) {
            array[i] = scanner.nextInt();
            suma += array[i];
        }

        // Afisare 1 - cu for clasic
        System.out.println("Afisare cu for clasic:");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();

        // Afisare 2 - cu enhanced for
        System.out.println("Afisare cu enhanced for:");
        for (int num : array) {
            System.out.print(num + " ");
        }
        System.out.println();

        double media = (double) suma / n;
        System.out.println("Media aritmetica este: " + media);

        scanner.close();
    }
}