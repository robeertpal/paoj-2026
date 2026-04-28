package com.pao.laboratory09.exercise3;

import java.util.concurrent.atomic.AtomicInteger;

public class ATMThread extends Thread {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    private final int atmId;
    private final CoadaTranzactii coada;

    public ATMThread(int atmId, CoadaTranzactii coada) {
        this.atmId = atmId;
        this.coada = coada;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= 4; i++) {
                int id = NEXT_ID.getAndIncrement();
                double suma = 100.0 * atmId + 25.5 * i;
                String data = "2024-05-14";

                Tranzactie tranzactie = new Tranzactie(id, suma, data, atmId);

                System.out.printf(
                        "[ATM-%d] trimite: Tranzactie #%d %.2f RON%n",
                        atmId,
                        tranzactie.getId(),
                        tranzactie.getSuma()
                );

                coada.adauga(tranzactie);

                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}