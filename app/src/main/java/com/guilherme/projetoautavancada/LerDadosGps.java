package com.guilherme.projetoautavancada;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LerDadosGps extends Thread {
    private final Context context;
    private final FusedLocationProviderClient locationClient;

    public LerDadosGps(Context context) {
        this.context = context;
        this.locationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        while (true) { // atualizar a localização
            locationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    ((MainActivity)context).runOnUiThread(() -> {
                        ((MainActivity)context).updateLocationUI(location.getLatitude(), location.getLongitude());
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
