package com.pao.laboratory07.exercise3;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("EXERCIȚIUL 3 BONUS");
        System.out.println("=================================================\n");

        List<Order> orders = buildSampleOrders();

        // 1. Afișare inițială comenzi
        System.out.println("1) LISTA INIȚIALĂ DE COMENZI");
        printOrders(orders);

        // 2. Grupare după tip + media valorilor
        System.out.println("\n2) MEDIA VALORILOR PE TIP DE COMANDĂ");
        Map<OrderType, Double> avgByType = OrderAnalytics.averageValueByType(orders);
        for (Map.Entry<OrderType, Double> entry : avgByType.entrySet()) {
            System.out.printf("Tip: %-8s | Media valorilor: %.2f%n", entry.getKey(), entry.getValue());
        }

        // 3. Comenzi peste media tuturor comenzilor
        System.out.println("\n3) COMENZI CU VALOARE PESTE MEDIA TUTUROR COMENZILOR");
        double overallAverage = OrderAnalytics.overallAverageValue(orders);
        System.out.printf("Media generală: %.2f%n", overallAverage);
        List<Order> aboveAverageOrders = OrderAnalytics.ordersAboveOverallAverage(orders);
        aboveAverageOrders.forEach(order -> System.out.println(" - " + order));

        // 4. Pentru fiecare comandă afișăm dacă este specială
        System.out.println("\n4) VERIFICARE COMENZI SPECIALE");
        for (Order order : orders) {
            System.out.printf("Comanda #%d (%s) -> specială: %s | detalii: %s%n",
                    order.getId(),
                    order.getType(),
                    order.isSpecial(),
                    order.specialInfo());
        }

        // 5. Filtrare după prag dat ca argument sau implicit 500
        double threshold = readThreshold(args);
        System.out.println("\n5) FILTRARE DUPĂ PRAG");
        System.out.printf("Prag folosit: %.2f%n", threshold);
        List<Order> filteredOrders = OrderAnalytics.filterByMinValue(orders, threshold);
        filteredOrders.forEach(order -> System.out.println(" - " + order));

        // 6. Sortare după client, apoi după valoare
        System.out.println("\n6) SORTARE DUPĂ CLIENT, APOI DUPĂ VALOARE");
        List<Order> sortedOrders = OrderAnalytics.sortByClientThenValue(orders);
        sortedOrders.forEach(order -> System.out.println(" - " + order));

        // 7. Workflow automat: procesarea precomenzilor cu livrare depășită
        System.out.println("\n7) WORKFLOW AUTOMAT - PRECOMENZI RESTANTE");
        System.out.println("Înainte de procesare:");
        orders.stream()
                .filter(order -> order instanceof Preorder)
                .forEach(System.out::println);

        OrderWorkflowService.processOverduePreorders(orders);

        System.out.println("După procesare:");
        orders.stream()
                .filter(order -> order instanceof Preorder)
                .forEach(System.out::println);

        // 8. Raport sumar pe tipuri
        System.out.println("\n8) RAPORT SUMAR");
        System.out.println(OrderAnalytics.summaryReport(orders));

        // 9. Raport extra: total valoare pe lună
        System.out.println("9) RAPORT EXTRA - TOTAL VALOARE PE LUNĂ");
        Map<Month, Double> monthlyTotals = OrderAnalytics.totalValueByMonth(orders);
        for (Map.Entry<Month, Double> entry : monthlyTotals.entrySet()) {
            System.out.printf("%s -> %.2f%n", entry.getKey(), entry.getValue());
        }

        // 10. Raport extra: total valoare pe client
        System.out.println("\n10) RAPORT EXTRA - TOTAL VALOARE PE CLIENT");
        Map<String, Double> clientTotals = OrderAnalytics.totalValueByClient(orders);
        for (Map.Entry<String, Double> entry : clientTotals.entrySet()) {
            System.out.printf("%s -> %.2f%n", entry.getKey(), entry.getValue());
        }

        // 11. Export CSV
        System.out.println("\n11) EXPORT CSV");
        String csv = OrderWorkflowService.exportToCsv(orders);
        System.out.println(csv);

        // 12. Tratarea erorilor / cazuri limită
        System.out.println("\n12) DEMONSTRARE ERORI ȘI CAZURI LIMITĂ");
        demonstrateErrors();

        System.out.println("\n=================================================");
        System.out.println("DEMO FINALIZAT CU SUCCES");
        System.out.println("=================================================");
    }

    private static List<Order> buildSampleOrders() {
        List<Order> orders = new ArrayList<>();

        orders.add(new StandardOrder(
                1, "Ana Popescu", 250.0,
                LocalDate.of(2026, 3, 10),
                OrderStatus.PLACED
        ));

        orders.add(new ExpressOrder(
                2, "Mihai Ionescu", 900.0,
                LocalDate.of(2026, 3, 11),
                OrderStatus.PROCESSING,
                24
        ));

        orders.add(new Preorder(
                3, "Ioana Georgescu", 1200.0,
                LocalDate.of(2026, 2, 20),
                OrderStatus.PLACED,
                LocalDate.now().minusDays(5) // restantă
        ));

        orders.add(new BulkOrder(
                4, "Firma Alpha SRL", 2500.0,
                LocalDate.of(2026, 1, 18),
                OrderStatus.SHIPPED,
                40
        ));

        orders.add(new StandardOrder(
                5, "Ana Popescu", 600.0,
                LocalDate.of(2026, 4, 2),
                OrderStatus.DELIVERED
        ));

        orders.add(new Preorder(
                6, "Radu Marin", 700.0,
                LocalDate.of(2026, 4, 1),
                OrderStatus.PLACED,
                LocalDate.now().plusDays(10) // încă nu e restantă
        ));

        orders.add(new ExpressOrder(
                7, "Bianca Tudor", 450.0,
                LocalDate.of(2026, 4, 4),
                OrderStatus.PLACED,
                12
        ));

        return orders;
    }

    private static void printOrders(List<Order> orders) {
        for (Order order : orders) {
            System.out.println(" - " + order);
        }
    }

    private static double readThreshold(String[] args) {
        double defaultThreshold = 500.0;

        if (args == null || args.length == 0) {
            System.out.println("Nu s-a primit argument în linia de comandă. Se folosește pragul implicit: 500.0");
            return defaultThreshold;
        }

        try {
            return Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Argument invalid pentru prag. Se folosește pragul implicit: 500.0");
            return defaultThreshold;
        }
    }

    private static void demonstrateErrors() {
        try {
            new StandardOrder(0, "Client Invalid", 100, LocalDate.now(), OrderStatus.PLACED);
        } catch (InvalidOrderDataException e) {
            System.out.println("Eroare capturată (id invalid): " + e.getMessage());
        }

        try {
            new StandardOrder(10, "", 100, LocalDate.now(), OrderStatus.PLACED);
        } catch (InvalidOrderDataException e) {
            System.out.println("Eroare capturată (nume client invalid): " + e.getMessage());
        }

        try {
            new StandardOrder(11, "Client X", -10, LocalDate.now(), OrderStatus.PLACED);
        } catch (InvalidOrderDataException e) {
            System.out.println("Eroare capturată (valoare negativă): " + e.getMessage());
        }

        try {
            OrderType.fromString("mystery");
        } catch (UnknownOrderTypeException e) {
            System.out.println("Eroare capturată (tip necunoscut): " + e.getMessage());
        }

        try {
            OrderAnalytics.filterByMinValue(buildSampleOrders(), -5);
        } catch (InvalidOrderDataException e) {
            System.out.println("Eroare capturată (prag negativ): " + e.getMessage());
        }

        try {
            OrderAnalytics.overallAverageValue(List.of());
        } catch (InvalidOrderDataException e) {
            System.out.println("Eroare capturată (listă goală): " + e.getMessage());
        }
    }
}