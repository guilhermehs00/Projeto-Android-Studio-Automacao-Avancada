package com.guilherme.projetoautavancada;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.guilherme.mylibrary.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class ConsultaBD extends Thread {
    private final FirebaseFirestore db;
    private final Double lati, longi;
    private final CallbackConsulta callback;
    public Region aux;
    private Region regiaoProxima = null;
    protected boolean RegProx = false;
    protected boolean SubRegProx = false;
    protected boolean RestRegProx = false;

    private long tempo_inicio_atividade;
    public double Time_T3;
    //private long tempo_inicio;
    //private long tempo_fim;

    public ConsultaBD(FirebaseFirestore db, Double lati, Double longi, CallbackConsulta callback, Region aux, long tempo_inicio_atividade) {
        this.db = db;
        this.lati = lati;
        this.longi = longi;
        this.callback = callback;
        this.aux = aux;
        this.tempo_inicio_atividade = tempo_inicio_atividade;
    }

    @Override
    public void run() {
        //tempo_inicio = System.nanoTime();
        db.collection("Regiões").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {

                    Region regionDescriptografada;
                    SubRegion SregionDescriptografada;
                    RestrictedRegion RregionDescriptografada;

                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        String nomeCripyted = documentSnapshot.getString("Nome ");
                        String latCrypted = documentSnapshot.getString("Latitude ");
                        String longCrypted = documentSnapshot.getString("Longitude ");
                        String userCrypeted = documentSnapshot.getString("User ");
                        String timeCrypeted = documentSnapshot.getString("Timestamp ");
                        String regionMainCrypted = documentSnapshot.getString("Região principal ");
                        String isRetrictedCrypted = documentSnapshot.getString("Restricted ");

                        DescriptografarDados descriptografarDados = descriptograr(nomeCripyted, latCrypted, longCrypted, userCrypeted, timeCrypeted, regionMainCrypted, isRetrictedCrypted);

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
                }

                Time_T3 = ((System.nanoTime() - tempo_inicio_atividade)/1_000_000_000.0);

                //tempo_fim = System.nanoTime();
                //System.out.println("Computação Consultar na BD: " + ((tempo_fim - tempo_inicio)/1_000_000_000.0) + " segundos.");
                callback.onResultado(RegProx, SubRegProx, RestRegProx, regiaoProxima, 0.0, Time_T3);
            } else {

                Time_T3 = ((System.nanoTime() - tempo_inicio_atividade)/1_000_000_000.0);

                //tempo_fim = System.nanoTime();
                //System.out.println("Computação Consultar na BD: " + ((tempo_fim - tempo_inicio)/1_000_000_000.0) + " segundos.");
                callback.onResultado(false, false, false, null, 0.0, Time_T3);

            }
        });
    }

    private DescriptografarDados descriptograr(String nomeCripyted, String latCrypted, String longCrypted, String userCrypeted, String timeCrypeted, String regionMainCrypted, String isRetrictedCrypted){
        DescriptografarDados descriptografarDados;
        if(isRetrictedCrypted != null){
            RestrictedRegion RR = new RestrictedRegion(
                    nomeCripyted,
                    latCrypted,
                    longCrypted,
                    userCrypeted,
                    timeCrypeted,
                    regionMainCrypted,
                    isRetrictedCrypted
            );
            descriptografarDados = new DescriptografarDados(RR);
        } else if (regionMainCrypted != null){
            SubRegion SR = new SubRegion(
                    nomeCripyted,
                    latCrypted,
                    longCrypted,
                    userCrypeted,
                    timeCrypeted,
                    regionMainCrypted
            );
            descriptografarDados = new DescriptografarDados(SR);
        }else{
            Region R = new Region(
                    nomeCripyted,
                    latCrypted,
                    longCrypted,
                    userCrypeted,
                    timeCrypeted
            );
            descriptografarDados = new DescriptografarDados(R);
        }
        descriptografarDados.start();
        try {
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
