package com.guilherme.projetoautavancada;

import com.guilherme.mylibrary.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ConsultaBD extends Thread {
    private FirebaseFirestore db;
    private Double lati, longi;
    private CallbackConsulta callback;
    public Region aux;


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
                boolean regProxima = false;
                boolean subRegProxima = false;
                boolean restRegProxima = false;
                Region regiaoProxima = null;

                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        String regionCriptografada = documentSnapshot.getString("RegionCriptografada"); // Corrigido para pegar o campo específico
                        Region regionDescriptografada = null;
                        try {
                            DescriptografarDados descriptografarDados = new DescriptografarDados(regionCriptografada);
                            descriptografarDados.start();
                            descriptografarDados.join();  // Aguarda a conclusão da thread de descriptografia
                            regionDescriptografada = descriptografarDados.getDecryptedJson();  // Obtém o resultado após a conclusão
                        } catch (InterruptedException e) {
                            System.err.println("Thread interrompida: " + e.getMessage());
                            e.printStackTrace();
                        }

                        if (regionDescriptografada.distance(lati, longi, regionDescriptografada.getLatitude(), regionDescriptografada.getLongitude())) {
                            if ("Region".equals(regionDescriptografada.getType())) {
                                regiaoProxima = regionDescriptografada;
                                aux = regiaoProxima;
                                regProxima = true;
                            } else if ("SubRegion".equals(regionDescriptografada.getType())) {
                                subRegProxima = true;
                            } else if ("RestrictedRegion".equals(regionDescriptografada.getType())) {
                                restRegProxima = true;
                            }
                        }
                    }
                }
                callback.onResultado(regProxima, subRegProxima, restRegProxima, regiaoProxima);
            } else {
                callback.onResultado(false, false, false, null);
            }
        });
    }
}
