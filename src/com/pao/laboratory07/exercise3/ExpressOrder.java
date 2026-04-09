package com.pao.laboratory07.exercise3;

import java.time.LocalDate;

public final class ExpressOrder extends Order implements SpecialOrder {
    private final int deliveryHours;

    public ExpressOrder(int id, String clientName, double value, LocalDate createdAt, OrderStatus status, int deliveryHours) {
        super(id, clientName, value, createdAt, status);

        if (deliveryHours <= 0) {
            throw new InvalidOrderDataException("Numărul de ore pentru livrare trebuie să fie pozitiv.");
        }

        this.deliveryHours = deliveryHours;
    }

    public int getDeliveryHours() {
        return deliveryHours;
    }

    @Override
    public OrderType getType() {
        return OrderType.EXPRESS;
    }

    @Override
    public String specialDescription() {
        return "Livrare expres în " + deliveryHours + "h";
    }
}