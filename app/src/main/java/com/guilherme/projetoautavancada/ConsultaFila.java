package com.guilherme.projetoautavancada;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.guilherme.mylibrary.*;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ConsultaFila extends Thread {
    private final Queue<Region> filaDeRegion;
    private final Semaphore semaphore;
    private final Double lati, longi;
    private final CallbackConsulta callback;
    public Region aux;
    private Region regiaoProxima = null;
    protected boolean RegProx = false;
    protected boolean SubRegProx = false;
    protected boolean RestRegProx = false;

    private long tempo_inicio_atividade;
    private double Time_T2;
    //private long tempo_inicio;
    //private long tempo_fim;

    public ConsultaFila(Queue<Region> filaDeRegion, Double lati, Double longi, Semaphore semaphore, CallbackConsulta callback, Region aux, long tempo_inicio_atividade) {
        this.filaDeRegion = filaDeRegion;
        this.semaphore = semaphore;
        this.lati = lati;
        this.longi = longi;
        this.callback = callback;
        this.aux = aux;
        this.tempo_inicio_atividade = tempo_inicio_atividade;
    }

    @Override
    public void run() {
        //tempo_inicio = System.nanoTime();
        try {
            semaphore.acquire();
            synchronized (filaDeRegion) {

                Region regionDescriptografada = null;
                SubRegion SregionDescriptografada = null;
                RestrictedRegion RregionDescriptografada = null;

                if (!filaDeRegion.isEmpty()) {
                    for (Region region : filaDeRegion) {

                        DescriptografarDados descriptografarDados = descriptograr(region);

                        if (descriptografarDados.getRegionDesCriptografada() != null){
                            regionDescriptografada = descriptografarDados.getRegionDesCriptografada();
                            calcularDistance(regionDescriptografada);
                        }else if(descriptografarDados.getSubRegionDesCriptografada() != null){
                            SregionDescriptografada = descriptografarDados.getSubRegionDesCriptografada();
                            calcularDistance(SregionDescriptografada);
                        }else{
                            RregionDescriptografada = descriptografarDados.getRestRegionDesCriptografada();
                            calcularDistance(RregionDescriptografada);
                        }
                    }

                    Time_T2 = ((System.nanoTime()  - tempo_inicio_atividade)/1_000_000_000.0);

                    //tempo_fim = System.nanoTime();
                    //System.out.println("Computação Consultar na Fila: " + ((tempo_fim - tempo_inicio)/1_000_000_000.0) + " segundos.");
                    callback.onResultado(RegProx, SubRegProx, RestRegProx, regiaoProxima, Time_T2, 0.0);

                } else {

                    Time_T2 = ((System.nanoTime() - tempo_inicio_atividade)/1_000_000_000.0);

                    //tempo_fim = System.nanoTime();
                    //System.out.println("Computação Consultar na Fila: " + ((tempo_fim - tempo_inicio)/1_000_000_000.0) + " segundos.");
                    callback.onResultado(false, false, false, null, Time_T2, 0.0);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    private DescriptografarDados descriptograr(Region region ){
        DescriptografarDados descriptografarDados;
        if (region instanceof SubRegion) {
            descriptografarDados = new DescriptografarDados((SubRegion) region);
        } else if (region instanceof RestrictedRegion) {
            descriptografarDados = new DescriptografarDados((RestrictedRegion) region);
        } else {
            descriptografarDados = new DescriptografarDados(region);
        }
        descriptografarDados.start();
        try{
            descriptografarDados.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        return descriptografarDados;
    }

    private void calcularDistance(Region r){
        if (r.distance(lati, longi, r.getLatitude(), r.getLongitude())) {
            if (r instanceof SubRegion) {
                SubRegProx = true;
            } else if (r instanceof RestrictedRegion) {
                RestRegProx = true;
            } else {
                RegProx = true;
                regiaoProxima = r;
                aux = regiaoProxima;
            }
        }
    }
}
