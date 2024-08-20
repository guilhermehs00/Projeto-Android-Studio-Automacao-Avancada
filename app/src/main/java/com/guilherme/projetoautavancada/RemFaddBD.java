package com.guilherme.projetoautavancada;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guilherme.mylibrary.DescriptografarDados;
import com.guilherme.mylibrary.Region;
import com.guilherme.mylibrary.RestrictedRegion;
import com.guilherme.mylibrary.SubRegion;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class RemFaddBD extends Thread {
    private final Queue<Region> filaDeRegion;
    private final Semaphore semaphore;
    private final Handler handler;
    private final Context context;
    private final TextView notification;
    private FirebaseFirestore db;

    public RemFaddBD(FirebaseFirestore db, Queue<Region> filaDeRegion, Semaphore semaphore, Handler handler, Context context, TextView notification) {
        this.db = db;
        this.filaDeRegion = filaDeRegion;
        this.semaphore = semaphore;
        this.handler = handler;
        this.context = context;
        this.notification = notification;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            synchronized (filaDeRegion) {
                while (!filaDeRegion.isEmpty()) {
                    AdicionaBD(filaDeRegion.peek());
                    filaDeRegion.remove();
                }
            }
        } catch (InterruptedException e) {
            System.err.println("\nErro ao descriptografar ou deserializar dados: \n" + e.getMessage());
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }
    private void AdicionaBD(Region r){
        try{
            DescriptografarDados d = new DescriptografarDados(r);
            d.start();
            d.join();

            Region regionDescriptografada = d.getRegionDesCriptografada();
            SubRegion sregionDescriptografada = d.getSubRegionDesCriptografada();
            RestrictedRegion rregionDescriptografada = d.getRestRegionDesCriptografada();

            String nomeDoc = " ";
            if (regionDescriptografada != null) {
                nomeDoc = regionDescriptografada.getName() ;
            } else if (sregionDescriptografada != null) {
                nomeDoc = sregionDescriptografada.getName();
            } else if (rregionDescriptografada != null) {
                nomeDoc = rregionDescriptografada.getName();
            }

            // Grava no Firestore
            DocumentReference docRef = db.collection("Y1").document(nomeDoc);
            docRef.set(r.serialize()).addOnSuccessListener(aVoid -> {
                notification.setText("Salvo no Firestore Database!");
                notification.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> notification.setVisibility(View.GONE), 1000);
            }).addOnFailureListener(e -> {
                handler.post(() -> Toast.makeText(context, "Erro ao gravar no BD: " + e.getMessage(), Toast.LENGTH_LONG).show());
            });
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
