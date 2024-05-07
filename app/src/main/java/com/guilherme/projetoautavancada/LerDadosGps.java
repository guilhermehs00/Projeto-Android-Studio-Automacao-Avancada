package com.guilherme.projetoautavancada;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LerDadosGps extends Thread {
    private final Context context;
    private final FusedLocationProviderClient locationClient;

    public long tempo_inicio_LerDados;

    public LerDadosGps(Context context) {
        this.context = context;
        this.locationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        while (true) {
            /*System.out.println("\nComeçou a Atividade...");*/tempo_inicio_LerDados = System.nanoTime();
            locationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    ((AtividadePrincipal)context).runOnUiThread(() -> {
                        ((AtividadePrincipal)context).atualizarLocalization(location.getLatitude(), location.getLongitude(), tempo_inicio_LerDados);
                    });
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
