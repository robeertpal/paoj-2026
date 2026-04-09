package com.pao.laboratory07.exercise3;

import java.time.LocalDate;
import java.util.Objects;

public sealed abstract class Order permits StandardOrder, ExpressOrder, Preorder, BulkOrder {
    private final int id;
    private final String clientName;
    private final double value;
    private final LocalDate createdAt;
    private OrderStatus status;

    protected Order(int id, String clientName, double value, LocalDate createdAt, OrderStatus status) {
        validateId(id);
        validateClientName(clientName);
        validateValue(value);
        validateCreatedAt(createdAt);
        this.id = id;
        this.clientName = clientName;
        this.value = value;
        this.createdAt = createdAt;
        this.status = Objects.requireNonNullElse(status, OrderStatus.PLACED);
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new InvalidOrderDataException("ID-ul comenzii trebuie să fie pozitiv.");
        }
    }

    private void validateClientName(String clientName) {
        if (clientName == null || clientName.isBlank()) {
            throw new InvalidOrderDataException("Numele clientului nu poate fi null sau gol.");
        }
    }

    private void validateValue(double value) {
        if (value <= 0) {
            throw new InvalidOrderDataException("Valoarea comenzii trebuie să fie pozitivă.");
        }
    }

    private void validateCreatedAt(LocalDate createdAt) {
        if (createdAt == null) {
            throw new InvalidOrderDataException("Data creării nu poate fi null.");
        }
    }

    public abstract OrderType getType();

    public int getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public double getValue() {
        return value;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void advanceStatus() {
        this.status = this.status.next();
    }

    public void cancel() {
        this.status = this.status.cancel();
    }

    public boolean isSpecial() {
        return this instanceof SpecialOrder;
    }

    public String specialInfo() {
        if (this instanceof SpecialOrder specialOrder) {
            return specialOrder.specialDescription();
        }
        return "Nu";
    }

    public String shortDescription() {
        return String.format(
                "Order{id=%d, client='%s', value=%.2f, type=%s, status=%s}",
                id, clientName, value, getType(), status
        );
    }

    @Override
    public String toString() {
        return shortDescription();
    }
}