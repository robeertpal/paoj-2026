package com.pao.laboratory07.exercise3;

import java.time.LocalDate;

public final class BulkOrder extends Order implements SpecialOrder {
    private final int itemCount;

    public BulkOrder(int id, String clientName, double value, LocalDate createdAt, OrderStatus status, int itemCount) {
        super(id, clientName, value, createdAt, status);

        if (itemCount <= 0) {
            throw new InvalidOrderDataException("Numărul de produse trebuie să fie pozitiv.");
        }

        this.itemCount = itemCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    @Override
    public OrderType getType() {
        return OrderType.BULK;
    }

    @Override
    public String specialDescription() {
        return "Comandă bulk cu " + itemCount + " produse";
    }
}