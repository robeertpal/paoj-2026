package com.pao.proiect.aibilet.model.dto;

public class ComandaClientView {
    private final int comandaId;
    private final String dataComanda;
    private final double total;
    private final int numarBilete;

    public ComandaClientView(int comandaId, String dataComanda, double total, int numarBilete) {
        this.comandaId = comandaId;
        this.dataComanda = dataComanda;
        this.total = total;
        this.numarBilete = numarBilete;
    }

    public int getComandaId() {
        return comandaId;
    }

    public String getDataComanda() {
        return dataComanda;
    }

    public double getTotal() {
        return total;
    }

    public int getNumarBilete() {
        return numarBilete;
    }

    @Override
    public String toString() {
        return "ComandaClientView{" +
                "comandaId=" + comandaId +
                ", dataComanda='" + dataComanda + '\'' +
                ", total=" + total +
                ", numarBilete=" + numarBilete +
                '}';
    }
}
