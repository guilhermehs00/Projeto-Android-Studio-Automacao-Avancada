package com.guilherme.projetoautavancada;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import com.guilherme.mylibrary.*;
import com.google.gson.Gson;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class RemFaddBD extends Thread {
    private final Queue<String> filaDeRegion;
    private final Semaphore semaphore;
    private final Handler handler;
    private final Context context;
    private final TextView labelsizeFila;
    private FirebaseFirestore db;
    private Gson gson = new Gson();

    public RemFaddBD(FirebaseFirestore db, Queue<String> filaDeRegion, Semaphore semaphore, Handler handler, Context context, TextView labelsizeFila) {
        this.db = db;
        this.filaDeRegion = filaDeRegion;
        this.semaphore = semaphore;
        this.handler = handler;
        this.context = context;
        this.labelsizeFila = labelsizeFila;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            synchronized (filaDeRegion) {
                while (!filaDeRegion.isEmpty()) {
                    try {
                        String encryptedRegionJson  = filaDeRegion.remove();
                        Region regionDescriptografada = null;
                        try {
                            DescriptografarDados descriptografarDados = new DescriptografarDados(encryptedRegionJson);
                            descriptografarDados.start();
                            descriptografarDados.join();  // Aguarda a conclusão da thread de descriptografia
                            regionDescriptografada = descriptografarDados.getDecryptedJson();  // Obtém o resultado após a conclusão

                        } catch (InterruptedException e) {
                            System.err.println("Thread interrompida: " + e.getMessage());
                            e.printStackTrace();
                        }

                        // Grava no Firestore
                        DocumentReference docRef = db.collection("Regiões").document(regionDescriptografada.getName());
                        docRef.set(regionDescriptografada.serialize(encryptedRegionJson)).addOnSuccessListener(aVoid -> {
                                    handler.post(() -> labelsizeFila.setText("Regiões na fila: " + filaDeRegion.size()));
                                }).addOnFailureListener(e -> {
                                    handler.post(() -> Toast.makeText(context, "Erro ao gravar no BD: " + e.getMessage(), Toast.LENGTH_LONG).show());
                                });
                    } catch (Exception e) {
                        System.err.println("Erro ao descriptografar ou deserializar dados: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }
}
