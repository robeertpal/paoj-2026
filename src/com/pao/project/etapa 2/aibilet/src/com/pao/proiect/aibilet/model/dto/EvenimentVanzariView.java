package com.pao.proiect.aibilet.model.dto;

public class EvenimentVanzariView {
    private final int evenimentId;
    private final String titluEveniment;
    private final int numarBileteVandute;
    private final double venitTotal;

    public EvenimentVanzariView(int evenimentId, String titluEveniment, int numarBileteVandute, double venitTotal) {
        this.evenimentId = evenimentId;
        this.titluEveniment = titluEveniment;
        this.numarBileteVandute = numarBileteVandute;
        this.venitTotal = venitTotal;
    }

    public int getEvenimentId() {
        return evenimentId;
    }

    public String getTitluEveniment() {
        return titluEveniment;
    }

    public int getNumarBileteVandute() {
        return numarBileteVandute;
    }

    public double getVenitTotal() {
        return venitTotal;
    }

    @Override
    public String toString() {
        return "EvenimentVanzariView{" +
                "evenimentId=" + evenimentId +
                ", titluEveniment='" + titluEveniment + '\'' +
                ", numarBileteVandute=" + numarBileteVandute +
                ", venitTotal=" + venitTotal +
                '}';
    }
}
