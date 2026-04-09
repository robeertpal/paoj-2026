package com.pao.laboratory07.exercise3;

import java.util.List;
import java.util.StringJoiner;

public final class OrderWorkflowService {
    private OrderWorkflowService() {
    }

    public static void processOverduePreorders(List<Order> orders) {
        if (orders == null) {
            throw new InvalidOrderDataException("Lista de comenzi nu poate fi null.");
        }

        orders.stream()
                .filter(order -> order instanceof Preorder)
                .map(order -> (Preorder) order)
                .filter(Preorder::isOverdue)
                .forEach(preorder -> {
                    preorder.setNotified(true);

                    if (preorder.getStatus() == OrderStatus.PLACED) {
                        preorder.advanceStatus(); // PLACED -> PROCESSING
                    }
                });
    }

    public static String exportToCsv(List<Order> orders) {
        if (orders == null) {
            throw new InvalidOrderDataException("Lista de comenzi nu poate fi null.");
        }

        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("id,client,value,type,status,createdAt,isSpecial,specialInfo");

        for (Order order : orders) {
            joiner.add(String.format("%d,%s,%.2f,%s,%s,%s,%s,%s",
                    order.getId(),
                    sanitize(order.getClientName()),
                    order.getValue(),
                    order.getType(),
                    order.getStatus(),
                    order.getCreatedAt(),
                    order.isSpecial(),
                    sanitize(order.specialInfo())
            ));
        }

        return joiner.toString();
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(",", " ");
    }
}