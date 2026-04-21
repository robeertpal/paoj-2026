package com.pao.laboratory08.exercise2;

import com.pao.laboratory08.exercise1.Adresa;
import com.pao.laboratory08.exercise1.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String FILE_PATH = "src/com/pao/laboratory08/tests/studenti.txt";
    private static final String OUTPUT_FILE = "rezultate.txt";

    public static void main(String[] args) throws Exception {
        List<Student> studenti = citesteStudentiDinFisier();

        Scanner scanner = new Scanner(System.in);
        int prag = scanner.nextInt();

        List<Student> studentiFiltrati = new ArrayList<>();

        for (Student student : studenti) {
            if (student.getVarsta() >= prag) {
                studentiFiltrati.add(student);
            }
        }

        BufferedWriter fout = new BufferedWriter(new FileWriter(OUTPUT_FILE));
        for (Student student : studentiFiltrati) {
            fout.write(student.toString());
            fout.newLine();
        }
        fout.close();

        System.out.println("Filtru: varsta >= " + prag);
        System.out.println("Rezultate: " + studentiFiltrati.size() + " studenti");
        System.out.println();

        for (Student student : studentiFiltrati) {
            System.out.println(student);
        }

        System.out.println();
        System.out.println("Scris in: rezultate.txt");
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
}