package com.pao.laboratory07.exercise3;

public enum OrderType {
    STANDARD,
    EXPRESS,
    PREORDER,
    BULK;

    public static OrderType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new UnknownOrderTypeException("Tipul comenzii nu poate fi null sau gol.");
        }

        try {
            return OrderType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownOrderTypeException("Tip necunoscut de comandă: " + value);
        }
    }
}