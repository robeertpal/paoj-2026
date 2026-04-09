package com.pao.laboratory07.exercise3;

public enum OrderStatus {
    PLACED {
        @Override
        public OrderStatus next() {
            return PROCESSING;
        }
    },
    PROCESSING {
        @Override
        public OrderStatus next() {
            return SHIPPED;
        }
    },
    SHIPPED {
        @Override
        public OrderStatus next() {
            return DELIVERED;
        }
    },
    DELIVERED {
        @Override
        public OrderStatus next() {
            return DELIVERED;
        }
    },
    CANCELLED {
        @Override
        public OrderStatus next() {
            return CANCELLED;
        }
    };

    public abstract OrderStatus next();

    public boolean isFinalState() {
        return this == DELIVERED || this == CANCELLED;
    }

    public OrderStatus cancel() {
        if (isFinalState()) {
            return this;
        }
        return CANCELLED;
    }
}