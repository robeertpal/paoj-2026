package com.pao.laboratory05.angajati;

/**
 * Exercise 3 — Angajați
 *
 * Cerințele complete se află în:
 *   src/com/pao/laboratory05/Readme.md  →  secțiunea "Exercise 3 — Angajați"
 *
 * Creează fișierele de la zero în acest pachet, apoi rulează Main.java
 * pentru a verifica output-ul așteptat din Readme.
 */

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AngajatService service = AngajatService.getInstance();

        while (true) {
            System.out.println("\n===== Gestionare Angajați =====");
            System.out.println("1. Adaugă angajat");
            System.out.println("2. Listare după salariu");
            System.out.println("3. Caută după departament");
            System.out.println("0. Ieșire");
            System.out.print("Opțiune: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1:
                    System.out.print("Nume: ");
                    String nume = scanner.nextLine();

                    System.out.print("Departament (nume): ");
                    String numeDepartament = scanner.nextLine();

                    System.out.print("Departament (locatie): ");
                    String locatieDepartament = scanner.nextLine();

                    System.out.print("Salariu: ");
                    double salariu = scanner.nextDouble();
                    scanner.nextLine();

                    Departament departament = new Departament(numeDepartament, locatieDepartament);
                    Angajat angajat = new Angajat(nume, departament, salariu);

                    service.addAngajat(angajat);
                    break;

                case 2:
                    service.listBySalary();
                    break;

                case 3:
                    System.out.print("Departament: ");
                    String departamentCautat = scanner.nextLine();
                    service.findByDepartament(departamentCautat);
                    break;

                case 0:
                    System.out.println("La revedere!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Opțiune invalidă.");
            }
        }
    }
}