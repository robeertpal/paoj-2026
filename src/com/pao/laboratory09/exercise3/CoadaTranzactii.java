package com.pao.laboratory09.exercise3;

import java.util.LinkedList;
import java.util.Queue;

public class CoadaTranzactii {
    private final Queue<Tranzactie> coada = new LinkedList<>();
    private final int capacitate;
    private boolean oprita = false;

    public CoadaTranzactii(int capacitate) {
        this.capacitate = capacitate;
    }

    public synchronized void adauga(Tranzactie t) throws InterruptedException {
        while (coada.size() == capacitate) {
            System.out.println("[ATM-" + t.getAtmId() + "] astept loc...");
            wait();
        }

        coada.add(t);
        notifyAll();
    }

    public synchronized Tranzactie extrage() throws InterruptedException {
        while (coada.isEmpty() && !oprita) {
            wait();
        }

        if (coada.isEmpty() && oprita) {
            return null;
        }

        Tranzactie t = coada.poll();
        notifyAll();
        return t;
    }

    public synchronized boolean esteGoala() {
        return coada.isEmpty();
    }

    public synchronized void opreste() {
        oprita = true;
        notifyAll();
    }
}