package com.guilherme.projetoautavancada;

import com.guilherme.mylibrary.DescriptografarDados;
import com.guilherme.mylibrary.Region;
import com.guilherme.mylibrary.RestrictedRegion;
import com.guilherme.mylibrary.SubRegion;

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

    public ConsultaFila(Queue<Region> filaDeRegion, Double lati, Double longi, Semaphore semaphore, CallbackConsulta callback, Region aux) {
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
