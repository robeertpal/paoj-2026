package com.pao.laboratory09.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex1.ser";

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String firstLine = br.readLine();
        if (firstLine == null || firstLine.trim().isEmpty()) {
            return;
        }

        int n = Integer.parseInt(firstLine.trim());
        List<Tranzactie> tranzactii = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String line = br.readLine();
            while (line != null && line.trim().isEmpty()) {
                line = br.readLine();
            }

            String[] parts = line.trim().split("\\s+");

            int id = Integer.parseInt(parts[0]);
            double suma = Double.parseDouble(parts[1]);
            String data = parts[2];
            String contSursa = parts[3];
            String contDestinatie = parts[4];
            TipTranzactie tip = TipTranzactie.valueOf(parts[5]);

            Tranzactie tranzactie = new Tranzactie(
                    id,
                    suma,
                    data,
                    contSursa,
                    contDestinatie,
                    tip
            );

            tranzactie.note = "procesat";
            tranzactii.add(tranzactie);
        }

        serializeaza(tranzactii);

        List<Tranzactie> tranzactiiDeserializate = deserializeaza();

        String commandLine;
        StringBuilder output = new StringBuilder();

        while ((commandLine = br.readLine()) != null) {
            commandLine = commandLine.trim();

            if (commandLine.isEmpty()) {
                continue;
            }

            String[] parts = commandLine.split("\\s+");
            String command = parts[0];

            switch (command) {
                case "LIST":
                    for (Tranzactie tranzactie : tranzactiiDeserializate) {
                        output.append(tranzactie.formatForOutput()).append('\n');
                    }
                    break;

                case "FILTER":
                    String prefix = parts[1];
                    boolean found = false;

                    for (Tranzactie tranzactie : tranzactiiDeserializate) {
                        if (tranzactie.data.startsWith(prefix)) {
                            output.append(tranzactie.formatForOutput()).append('\n');
                            found = true;
                        }
                    }

                    if (!found) {
                        output.append("Niciun rezultat.").append('\n');
                    }
                    break;

                case "NOTE":
                    int searchedId = Integer.parseInt(parts[1]);
                    Tranzactie foundTransaction = null;

                    for (Tranzactie tranzactie : tranzactiiDeserializate) {
                        if (tranzactie.id == searchedId) {
                            foundTransaction = tranzactie;
                            break;
                        }
                    }

                    if (foundTransaction == null) {
                        output.append("NOTE[")
                                .append(searchedId)
                                .append("]: not found")
                                .append('\n');
                    } else {
                        output.append("NOTE[")
                                .append(searchedId)
                                .append("]: ")
                                .append(foundTransaction.note)
                                .append('\n');
                    }
                    break;

                default:
                    break;
            }
        }

        System.out.print(output);
    }

    private static void serializeaza(List<Tranzactie> tranzactii) throws IOException {
        File file = new File(OUTPUT_FILE);
        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(tranzactii);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Tranzactie> deserializeaza() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(OUTPUT_FILE))) {
            return (List<Tranzactie>) in.readObject();
        }
    }
}