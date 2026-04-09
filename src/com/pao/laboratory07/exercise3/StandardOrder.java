package com.pao.laboratory07.exercise3;

import java.time.LocalDate;

public final class StandardOrder extends Order {
    public StandardOrder(int id, String clientName, double value, LocalDate createdAt, OrderStatus status) {
        super(id, clientName, value, createdAt, status);
    }

    @Override
    public OrderType getType() {
        return OrderType.STANDARD;
    }
}