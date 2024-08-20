package com.guilherme.projetoautavancada;

import com.guilherme.mylibrary.Region;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class AdicionarFila extends Thread {
    private final Queue<Region> filaDeRegion;
    private Region region;
    private final Semaphore semaphore;

    public AdicionarFila(Queue<Region> filaDeRegion, Region region, Semaphore semaphore) {
        this.filaDeRegion = filaDeRegion;
        this.region = region;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            synchronized (filaDeRegion) {
                filaDeRegion.add(region);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }
}
