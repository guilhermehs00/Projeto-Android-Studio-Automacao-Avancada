package com.guilherme.projetoautavancada;

import java.util.Queue;
import java.util.concurrent.Semaphore;
import com.guilherme.mylibrary.*;

public class AdicionarFila extends Thread {
    private final Queue<Region> filaDeRegion;
    private Region region;
    private final Semaphore semaphore;

    //private long tempo_inicio;
    //private long tempo_fim;

    public AdicionarFila(Queue<Region> filaDeRegion, Region region, Semaphore semaphore) {
        this.filaDeRegion = filaDeRegion;
        this.region = region;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        //tempo_inicio = System.nanoTime();
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
        //tempo_fim = System.nanoTime();
        //.out.println("Computação Adicionar na Fila: " + ((tempo_fim - tempo_inicio)/1_000_000_000.0) + " segundos.");
    }
}
