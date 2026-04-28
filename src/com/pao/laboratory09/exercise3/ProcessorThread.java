package com.pao.laboratory09.exercise3;

public class ProcessorThread implements Runnable {
    private final CoadaTranzactii coada;

    public volatile boolean activ = true;

    private int totalProcesate = 0;

    public ProcessorThread(CoadaTranzactii coada) {
        this.coada = coada;
    }

    @Override
    public void run() {
        try {
            while (activ || !coada.esteGoala()) {
                Tranzactie tranzactie = coada.extrage();

                if (tranzactie == null) {
                    break;
                }

                Thread.sleep(80);

                System.out.printf(
                        "[Processor] Factura #%d - %.2f RON | %s%n",
                        tranzactie.getId(),
                        tranzactie.getSuma(),
                        tranzactie.getData()
                );

                totalProcesate++;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getTotalProcesate() {
        return totalProcesate;
    }
}