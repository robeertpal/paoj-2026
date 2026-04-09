package com.pao.laboratory07.exercise3;

public interface SpecialOrder {
    default boolean isSpecial() {
        return true;
    }

    default String specialDescription() {
        return "Comandă specială";
    }
}