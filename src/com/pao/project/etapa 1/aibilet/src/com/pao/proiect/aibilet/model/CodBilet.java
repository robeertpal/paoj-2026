package com.pao.proiect.aibilet.model;

public final class CodBilet {

    private final String valoare;

    public CodBilet(String valoare) {
        if (valoare == null || valoare.trim().length() == 0) {
            throw new IllegalArgumentException("Codul de bilet nu poate fi null.");
        }

        this.valoare = valoare;
    }

    public String getValoare() {
        return valoare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CodBilet)) {
            return false;
        }

        CodBilet codBilet = (CodBilet) o;

        return valoare.equals(codBilet.valoare);
    }

    @Override
    public int hashCode() {
        return valoare.hashCode();
    }

    @Override
    public String toString() {
        return valoare;
    }
}