package com.guilherme.projetoautavancada;

import com.guilherme.mylibrary.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class ConsultaBD extends Thread {
    private FirebaseFirestore db;
    private Double lati, longi;
    private CallbackConsulta callback;
    public Region aux;
    private Region regiaoProxima = null;
    protected boolean RegProx = false;
    protected boolean SubRegProx = false;
    protected boolean RestRegProx = false;

    public ConsultaBD(FirebaseFirestore db, Double lati, Double longi, CallbackConsulta callback, Region aux) {
        this.db = db;
        this.lati = lati;
        this.longi = longi;
        this.callback = callback;
        this.aux = aux;
    }

    @Override
    public void run() {
        db.collection("Regiões").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try{
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {

                        Region regionDescriptografada = null;
                        SubRegion SregionDescriptografada = null;
                        RestrictedRegion RregionDescriptografada = null;

                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            String nomeCripyted = documentSnapshot.getString("Nome ");
                            String latCrypted = documentSnapshot.getString("Latitude ");
                            String longCrypted = documentSnapshot.getString("Longitude ");
                            String userCrypeted = documentSnapshot.getString("User ");
                            String timeCrypeted = documentSnapshot.getString("Timestamp ");
                            String regionMainCrypted = documentSnapshot.getString("Região principal ");
                            String isRetrictedCrypted = documentSnapshot.getString("Restricted ");

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
                            descriptografarDados.join();

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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                callback.onResultado(RegProx, SubRegProx, RestRegProx, regiaoProxima);
            } else {
                callback.onResultado(false, false, false, null);
            }
        });
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
