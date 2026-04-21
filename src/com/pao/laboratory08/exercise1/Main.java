package com.pao.laboratory08.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    private static final String FILE_PATH = "src/com/pao/laboratory08/tests/studenti.txt";

    public static void main(String[] args) throws Exception {
        List<Student> studenti = citesteStudentiDinFisier();

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String comanda = console.readLine();

        if (comanda == null || comanda.trim().isEmpty()) {
            return;
        }

        String[] parts = comanda.split(" ", 2);
        String tipComanda = parts[0].trim();

        if ("PRINT".equals(tipComanda)) {
            for (Student student : studenti) {
                System.out.println(student);
            }
        } else if ("SHALLOW".equals(tipComanda)) {
            if (parts.length < 2) {
                return;
            }

            String numeCautat = parts[1].trim();
            Student original = gasesteStudentDupaNume(studenti, numeCautat);

            if (original == null) {
                return;
            }

            Student clona = original.shallowClone();
            clona.getAdresa().setOras("MODIFICAT");

            System.out.println("Original: " + original);
            System.out.println("Clona: " + clona);

        } else if ("DEEP".equals(tipComanda)) {
            if (parts.length < 2) {
                return;
            }

            String numeCautat = parts[1].trim();
            Student original = gasesteStudentDupaNume(studenti, numeCautat);

            if (original == null) {
                return;
            }

            Student clona = original.deepClone();
            clona.getAdresa().setOras("MODIFICAT");

            System.out.println("Original: " + original);
            System.out.println("Clona: " + clona);
        }
    }

    private static List<Student> citesteStudentiDinFisier() throws IOException {
        List<Student> studenti = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));
        String linie;

        while ((linie = br.readLine()) != null) {
            linie = linie.trim();

            if (linie.isEmpty()) {
                continue;
            }

            String[] parts = linie.split(",");

            if (parts.length != 4) {
                continue;
            }

            String nume = parts[0].trim();
            int varsta = Integer.parseInt(parts[1].trim());
            String oras = parts[2].trim();
            String strada = parts[3].trim();

            Adresa adresa = new Adresa(oras, strada);
            Student student = new Student(nume, varsta, adresa);

            studenti.add(student);
        }

        br.close();
        return studenti;
    }

    private static Student gasesteStudentDupaNume(List<Student> studenti, String nume) {
        for (Student student : studenti) {
            if (student.getNume().equals(nume)) {
                return student;
            }
        }
        return null;
    }
}