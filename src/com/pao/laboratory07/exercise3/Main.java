package com.pao.laboratory07.exercise3;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== EXERCITIUL 3 BONUS - ANALIZA AVANSATA SI WORKFLOW-URI ===");
        System.out.println();

        double pragValoare = 200.0;
        if (args.length > 0) {
            try {
                pragValoare = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("[AVERTISMENT] Argument invalid pentru prag. Se foloseste pragul implicit: 200.0");
            }
        }
        final double pragFinal = pragValoare;

        List<Comanda> comenzi = new ArrayList<>();

        System.out.println("1. CREARE COMENZI SI VALIDARE");
        System.out.println("--------------------------------");

        try {
            comenzi.add(new ComandaStandard(1001, "Popescu", 250.0));
            comenzi.add(new Precomanda(1002, "Ionescu", 400.0, "2025-05-10"));
            comenzi.add(new ComandaAbonament(1003, "Georgescu", 120.0, 6));
            comenzi.add(new ComandaStandard(1004, "Enache", 300.0));
            comenzi.add(new Precomanda(1005, "Vasilescu", 500.0, "2027-06-01"));
            comenzi.add(new ComandaAbonament(1006, "Pop", 90.0, 12));
            comenzi.add(new Precomanda(1007, "Marin", 150.0, "2024-01-15"));
            comenzi.add(new ComandaStandard(1008, "Andrei", 700.0));

            for (Comanda c : comenzi) {
                System.out.println("Adaugata: " + c + " | speciala = " + c.esteSpeciala());
            }
        } catch (DateException | ValidationException e) {
            System.out.println("[EROARE LA CREARE] " + e.getMessage());
        }

        System.out.println();

        System.out.println("2. DEMONSTRARE CAZURI LIMITA SI ERORI");
        System.out.println("--------------------------------------");

        incearcaCreare(() -> new ComandaStandard(2001, "", 100.0), "client gol");
        incearcaCreare(() -> new ComandaStandard(2002, "Test", -50.0), "valoare negativa");
        incearcaCreare(() -> new Precomanda(2003, "Test2", 100.0, "data-gresita"), "data invalida");
        incearcaCreare(() -> new ComandaAbonament(2004, "Test3", 100.0, 0), "numar luni invalid");

        System.out.println();

        System.out.println("3. AFISARE SI PROCESARE POLIMORFICA");
        System.out.println("-----------------------------------");
        for (Comanda c : comenzi) {
            c.proceseaza();
        }

        System.out.println();

        System.out.println("4. GRUPARE DUPA TIP SI MEDIA VALORILOR");
        System.out.println("--------------------------------------");

        Map<String, Double> mediiPeTip = comenzi.stream()
                .collect(Collectors.groupingBy(
                        Comanda::tipComanda,
                        LinkedHashMap::new,
                        Collectors.averagingDouble(Comanda::getValoare)
                ));

        for (Map.Entry<String, Double> entry : mediiPeTip.entrySet()) {
            System.out.printf("%s -> media valorilor: %.2f lei%n", entry.getKey(), entry.getValue());
        }

        System.out.println();

        System.out.println("5. COMENZI CU VALOARE PESTE MEDIA TUTUROR COMENZILOR");
        System.out.println("----------------------------------------------------");

        double mediaTotala = comenzi.stream()
                .mapToDouble(Comanda::getValoare)
                .average()
                .orElse(0.0);

        System.out.printf("Media tuturor comenzilor: %.2f lei%n", mediaTotala);

        List<Comanda> pesteMedie = comenzi.stream()
                .filter(c -> c.getValoare() > mediaTotala)
                .toList();

        if (pesteMedie.isEmpty()) {
            System.out.println("Nu exista comenzi peste media generala.");
        } else {
            for (Comanda c : pesteMedie) {
                System.out.println(c);
            }
        }

        System.out.println();

        System.out.println("6. FILTRARE DUPA PRAG DE VALOARE");
        System.out.println("--------------------------------");
        System.out.printf("Prag folosit: %.2f lei%n", pragFinal);

        List<Comanda> filtrate = comenzi.stream()
                .filter(c -> c.getValoare() >= pragFinal)
                .toList();

        if (filtrate.isEmpty()) {
            System.out.println("Nu exista comenzi care sa respecte pragul.");
        } else {
            for (Comanda c : filtrate) {
                System.out.println(c);
            }
        }

        System.out.println();

        System.out.println("7. SORTARE DUPA CLIENT, APOI DUPA VALOARE");
        System.out.println("-----------------------------------------");

        List<Comanda> sortate = new ArrayList<>(comenzi);
        sortate.sort(
                Comparator.comparing(Comanda::getClient)
                        .thenComparing(Comanda::getValoare)
        );

        for (Comanda c : sortate) {
            System.out.println(c);
        }

        System.out.println();

        System.out.println("8. WORKFLOW AUTOMAT - PRECOMENZI CU LIVRARE DEPASITA");
        System.out.println("----------------------------------------------------");

        List<Precomanda> precomenziDepasite = comenzi.stream()
                .filter(c -> c instanceof Precomanda)
                .map(c -> (Precomanda) c)
                .filter(Precomanda::areLivrareDepasita)
                .toList();

        if (precomenziDepasite.isEmpty()) {
            System.out.println("Nu exista precomenzi cu termen depasit.");
        } else {
            for (Precomanda p : precomenziDepasite) {
                System.out.println("NOTIFICARE: precomanda intarziata -> " + p);
            }
        }

        System.out.println();

        System.out.println("9. RAPORT SUMAR PE TIP DE COMANDA");
        System.out.println("---------------------------------");

        raportPeTip(comenzi);

        System.out.println();

        System.out.println("10. EXPORT CSV (SIMULARE)");
        System.out.println("-------------------------");
        System.out.println(genereazaCSV(comenzi));

        System.out.println("=== SFARSIT DEMONSTRATIE ===");
    }

    private static void incearcaCreare(CommandFactory factory, String descriere) {
        try {
            factory.create();
            System.out.println("[NEASTEPTAT] Nu a aparut eroare pentru cazul: " + descriere);
        } catch (Exception e) {
            System.out.println("[OK] Eroare tratata pentru " + descriere + ": " + e.getMessage());
        }
    }

    private static void raportPeTip(List<Comanda> comenzi) {
        Map<String, List<Comanda>> grupate = comenzi.stream()
                .collect(Collectors.groupingBy(Comanda::tipComanda, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<String, List<Comanda>> entry : grupate.entrySet()) {
            String tip = entry.getKey();
            List<Comanda> lista = entry.getValue();

            double suma = lista.stream().mapToDouble(Comanda::getValoare).sum();
            double media = lista.stream().mapToDouble(Comanda::getValoare).average().orElse(0.0);
            long speciale = lista.stream().filter(Comanda::esteSpeciala).count();

            System.out.printf(
                    "%s -> numar: %d, suma: %.2f lei, media: %.2f lei, speciale: %d%n",
                    tip, lista.size(), suma, media, speciale
            );
        }
    }

    private static String genereazaCSV(List<Comanda> comenzi) {
        StringBuilder sb = new StringBuilder();
        sb.append("tip,id,client,valoare,speciala,detalii\n");

        for (Comanda c : comenzi) {
            sb.append(c.tipComanda()).append(",")
                    .append(c.getId()).append(",")
                    .append(c.getClient()).append(",")
                    .append(String.format(Locale.US, "%.2f", c.getValoare())).append(",")
                    .append(c.esteSpeciala()).append(",")
                    .append(c.detaliiCSV())
                    .append("\n");
        }

        return sb.toString();
    }

    @FunctionalInterface
    interface CommandFactory {
        Comanda create() throws Exception;
    }

    interface ActiuneComanda {
        void proceseaza();
        void afiseaza();
        String tipComanda();

        default boolean esteSpeciala() {
            return false;
        }
    }

    enum TipComanda {
        STANDARD,
        PRECOMANDA,
        ABONAMENT
    }

    static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    static class DateException extends Exception {
        public DateException(String message) {
            super(message);
        }
    }

    static abstract sealed class Comanda implements ActiuneComanda
            permits ComandaStandard, Precomanda, ComandaAbonament {

        protected int id;
        protected String client;
        protected double valoare;

        public Comanda(int id, String client, double valoare) throws ValidationException {
            valideazaId(id);
            valideazaClient(client);
            valideazaValoare(valoare);

            this.id = id;
            this.client = client;
            this.valoare = valoare;
        }

        public int getId() {
            return id;
        }

        public String getClient() {
            return client;
        }

        public double getValoare() {
            return valoare;
        }

        protected void valideazaId(int id) throws ValidationException {
            if (id <= 0) {
                throw new ValidationException("ID invalid: trebuie sa fie pozitiv.");
            }
        }

        protected void valideazaClient(String client) throws ValidationException {
            if (client == null || client.isBlank()) {
                throw new ValidationException("Numele clientului nu poate fi gol.");
            }
        }

        protected void valideazaValoare(double valoare) throws ValidationException {
            if (valoare <= 0) {
                throw new ValidationException("Valoarea comenzii trebuie sa fie pozitiva.");
            }
        }

        public abstract String detaliiCSV();
    }

    static final class ComandaStandard extends Comanda {

        public ComandaStandard(int id, String client, double valoare) throws ValidationException {
            super(id, client, valoare);
        }

        @Override
        public void proceseaza() {
            System.out.println("[PROCESARE STANDARD] " + this);
        }

        @Override
        public void afiseaza() {
            System.out.println(this);
        }

        @Override
        public String tipComanda() {
            return TipComanda.STANDARD.name();
        }

        @Override
        public String detaliiCSV() {
            return "fara_detalii_suplimentare";
        }

        @Override
        public String toString() {
            return String.format("STANDARD: %d %s, valoare: %.2f lei", id, client, valoare);
        }
    }

    static final class Precomanda extends Comanda {

        private LocalDate dataLivrare;

        public Precomanda(int id, String client, double valoare, String dataLivrare)
                throws ValidationException, DateException {
            super(id, client, valoare);
            this.dataLivrare = parseazaData(dataLivrare);
        }

        private LocalDate parseazaData(String data) throws DateException {
            try {
                return LocalDate.parse(data);
            } catch (DateTimeParseException e) {
                throw new DateException("Data de livrare invalida: " + data);
            }
        }

        public boolean areLivrareDepasita() {
            return dataLivrare.isBefore(LocalDate.now());
        }

        @Override
        public void proceseaza() {
            System.out.println("[PROCESARE PRECOMANDA] " + this);
        }

        @Override
        public void afiseaza() {
            System.out.println(this);
        }

        @Override
        public String tipComanda() {
            return TipComanda.PRECOMANDA.name();
        }

        @Override
        public boolean esteSpeciala() {
            return true;
        }

        @Override
        public String detaliiCSV() {
            return "livrare=" + dataLivrare;
        }

        @Override
        public String toString() {
            return String.format(
                    "PRECOMANDA: %d %s, valoare: %.2f lei, livrare: %s",
                    id, client, valoare, dataLivrare
            );
        }
    }

    static final class ComandaAbonament extends Comanda {

        private int nrLuni;

        public ComandaAbonament(int id, String client, double valoare, int nrLuni)
                throws ValidationException {
            super(id, client, valoare);
            if (nrLuni <= 0) {
                throw new ValidationException("Numarul de luni trebuie sa fie pozitiv.");
            }
            this.nrLuni = nrLuni;
        }

        @Override
        public void proceseaza() {
            System.out.println("[PROCESARE ABONAMENT] " + this);
        }

        @Override
        public void afiseaza() {
            System.out.println(this);
        }

        @Override
        public String tipComanda() {
            return TipComanda.ABONAMENT.name();
        }

        @Override
        public boolean esteSpeciala() {
            return true;
        }

        @Override
        public String detaliiCSV() {
            return "luni=" + nrLuni;
        }

        @Override
        public String toString() {
            return String.format(
                    "ABONAMENT: %d %s, valoare: %.2f lei, luni: %d",
                    id, client, valoare, nrLuni
            );
        }
    }
}