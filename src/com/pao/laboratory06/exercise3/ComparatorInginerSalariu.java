package com.pao.laboratory06.exercise3;

import java.util.Comparator;

public class ComparatorInginerSalariu implements Comparator<Inginer> {

    @Override
    public int compare(Inginer i1, Inginer i2) {
        if (i1 == null || i2 == null) {
            throw new IllegalArgumentException("Inginerii comparati nu pot fi null.");
        }

        return Double.compare(i2.getSalariu(), i1.getSalariu()); // descrescator
    }
}