package com.pao.laboratory06.exercise1;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int numarAngajati = scanner.nextInt();
        Angajat[] angajati = new Angajat[numarAngajati];

        for (int i = 0; i < numarAngajati; i++) {
            angajati[i] = Angajat.citeste(scanner);
        }

        String optiune = scanner.next();

        Comparator<Angajat> comparator = switch (optiune) {
            case "by_name" -> (a1, a2) -> a1.getNume().compareTo(a2.getNume());
            case "by_salary" -> (a1, a2) -> Double.compare(a1.getSalariu(), a2.getSalariu());
            case "by_salary_desc" -> (a1, a2) -> Double.compare(a2.getSalariu(), a1.getSalariu());
            default -> (a1, a2) -> 0;
        };

        Arrays.sort(angajati, comparator);

        for (Angajat angajat : angajati) {
            System.out.println(angajat);
        }
    }
}