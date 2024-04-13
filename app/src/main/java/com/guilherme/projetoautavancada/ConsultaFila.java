package com.guilherme.projetoautavancada;

import com.guilherme.mylibrary.*;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ConsultaFila extends Thread {
    private Queue<String> filaDeRegion;
    private final Semaphore semaphore;
    private Double lati, longi;
    private CallbackConsulta callback;
    public Region aux;

    public ConsultaFila(Queue<String> filaDeRegion, Double lati, Double longi, Semaphore semaphore, CallbackConsulta callback, Region aux) {
        this.filaDeRegion = filaDeRegion;
        this.semaphore = semaphore;
        this.lati = lati;
        this.longi = longi;
        this.callback = callback;
        this.aux = aux;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            synchronized (filaDeRegion) {
                boolean RegProx = false;
                boolean SubRegProx = false;
                boolean RestRegProx = false;
                Region regiaoProxima = null;

                if (!filaDeRegion.isEmpty()) {
                    for (String regionJsonCriptografada : filaDeRegion) {
                        try {
                            DescriptografarDados descriptografarDados = new DescriptografarDados(regionJsonCriptografada);
                            descriptografarDados.start();
                            descriptografarDados.join();
                            Region regionDescriptografada = descriptografarDados.getDecryptedJson();
                            if (regionDescriptografada.distance(lati, longi, regionDescriptografada.getLatitude(), regionDescriptografada.getLongitude())) {
                                if ("SubRegion".equals(regionDescriptografada.getType())) {
                                    SubRegProx = true;
                                } else if ("RestrictedRegion".equals(regionDescriptografada.getType())) {
                                    RestRegProx = true;
                                } else {
                                    RegProx = true;
                                    regiaoProxima = regionDescriptografada;
                                    aux = regiaoProxima;
                                }
                            }
                        } catch (InterruptedException e) {
                            System.err.println("Thread interrompida: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    callback.onResultado(RegProx, SubRegProx, RestRegProx, regiaoProxima);
                } else {
                    callback.onResultado(false, false, false, null);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }
}
