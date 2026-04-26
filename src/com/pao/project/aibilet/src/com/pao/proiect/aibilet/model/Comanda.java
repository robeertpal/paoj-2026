package com.pao.proiect.aibilet.model;

public class Comanda {
    private int id;
    private final int clientId;
    private final int[] ticketIds;
    private final double total;
    private final String timestamp;

    public Comanda(int id, int clientId, int[] ticketIds, double total, String timestamp) {
        if (ticketIds == null) {
            throw new IllegalArgumentException("Ticket Id-ul nu poate fi null.");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp-ul nu poate fi null.");
        }

        this.id = id;
        this.clientId = clientId;
        this.ticketIds = new int[ticketIds.length];

        for (int i = 0; i < ticketIds.length; i++) {
            this.ticketIds[i] = ticketIds[i];
        }

        this.total = total;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public int[] getTicketIds() {
        int[] copie = new int[ticketIds.length];

        for (int i = 0; i < ticketIds.length; i++) {
            copie[i] = ticketIds[i];
        }

        return copie;
    }

    public double getTotal() {
        return total;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        String bileteText = "";

        for (int i = 0; i < ticketIds.length; i++) {
            bileteText = bileteText + ticketIds[i];

            if (i < ticketIds.length - 1) {
                bileteText = bileteText + ", ";
            }
        }

        return "Comanda\n" +
                "  ID: " + id + "\n" +
                "  Client ID: " + clientId + "\n" +
                "  ID bilete: " + bileteText + "\n" +
                "  Numar bilete: " + ticketIds.length + "\n" +
                "  Total: " + total + "\n" +
                "  Timestamp: " + timestamp;
    }
}