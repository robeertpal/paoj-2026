package com.pao.laboratory07.exercise3;

import java.time.LocalDate;

public final class Preorder extends Order implements SpecialOrder {
    private LocalDate expectedDeliveryDate;
    private boolean notified;

    public Preorder(int id, String clientName, double value, LocalDate createdAt,
                    OrderStatus status, LocalDate expectedDeliveryDate) {
        super(id, clientName, value, createdAt, status);

        if (expectedDeliveryDate == null) {
            throw new InvalidOrderDataException("Data estimată de livrare nu poate fi null.");
        }

        this.expectedDeliveryDate = expectedDeliveryDate;
        this.notified = false;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public boolean isOverdue() {
        return expectedDeliveryDate.isBefore(LocalDate.now())
                && !getStatus().isFinalState();
    }

    @Override
    public OrderType getType() {
        return OrderType.PREORDER;
    }

    @Override
    public String specialDescription() {
        return "Precomandă cu livrare estimată la " + expectedDeliveryDate;
    }

    @Override
    public String toString() {
        return super.toString() +
                String.format(", expectedDeliveryDate=%s, notified=%s",
                        expectedDeliveryDate, notified);
    }
}