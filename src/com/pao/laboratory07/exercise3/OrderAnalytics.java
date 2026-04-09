package com.pao.laboratory07.exercise3;

import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class OrderAnalytics {
    private OrderAnalytics() {
    }

    public static Map<OrderType, Double> averageValueByType(List<Order> orders) {
        validateOrders(orders);

        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getType,
                        Collectors.averagingDouble(Order::getValue)
                ));
    }

    public static double overallAverageValue(List<Order> orders) {
        validateOrders(orders);

        return orders.stream()
                .mapToDouble(Order::getValue)
                .average()
                .orElse(0.0);
    }

    public static List<Order> ordersAboveOverallAverage(List<Order> orders) {
        validateOrders(orders);

        double avg = overallAverageValue(orders);

        return orders.stream()
                .filter(order -> order.getValue() > avg)
                .toList();
    }

    public static List<Order> filterByMinValue(List<Order> orders, double minValue) {
        validateOrders(orders);

        if (minValue < 0) {
            throw new InvalidOrderDataException("Pragul minim nu poate fi negativ.");
        }

        return orders.stream()
                .filter(order -> order.getValue() >= minValue)
                .toList();
    }

    public static List<Order> sortByClientThenValue(List<Order> orders) {
        validateOrders(orders);

        return orders.stream()
                .sorted(Comparator
                        .comparing(Order::getClientName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Order::getValue))
                .toList();
    }

    public static Map<Month, Double> totalValueByMonth(List<Order> orders) {
        validateOrders(orders);

        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().getMonth(),
                        Collectors.summingDouble(Order::getValue)
                ));
    }

    public static Map<String, Double> totalValueByClient(List<Order> orders) {
        validateOrders(orders);

        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getClientName,
                        Collectors.summingDouble(Order::getValue)
                ));
    }

    public static String summaryReport(List<Order> orders) {
        validateOrders(orders);

        StringBuilder sb = new StringBuilder();
        sb.append("=== RAPORT SUMAR PE TIPURI DE COMENZI ===\n");

        Map<OrderType, List<Order>> byType = orders.stream()
                .collect(Collectors.groupingBy(Order::getType));

        for (OrderType type : OrderType.values()) {
            List<Order> current = byType.getOrDefault(type, List.of());
            double total = current.stream().mapToDouble(Order::getValue).sum();
            double average = current.stream().mapToDouble(Order::getValue).average().orElse(0.0);

            sb.append(String.format(
                    "%s -> count=%d, total=%.2f, average=%.2f%n",
                    type, current.size(), total, average
            ));
        }

        return sb.toString();
    }

    private static void validateOrders(List<Order> orders) {
        if (orders == null) {
            throw new InvalidOrderDataException("Lista de comenzi nu poate fi null.");
        }
        if (orders.isEmpty()) {
            throw new InvalidOrderDataException("Lista de comenzi nu poate fi goală.");
        }
    }
}