package com.guilherme.projetoautavancada;

import com.google.gson.Gson;
import com.guilherme.mylibrary.*;

public class DescriptografarDados extends Thread {
    private static final Gson gson = new Gson();  // Static para reutilização
    private String regionCriptografada;
    private volatile Region regionJsonDescriptografada;  // Volatile para visibilidade entre threads

    public DescriptografarDados(String regionCriptografada) {
        this.regionCriptografada = regionCriptografada;
    }

    @Override
    public void run() {
        try {
            String decryptedRegionJson = Cryptography.decrypt(regionCriptografada); // Descriptografa
            this.regionJsonDescriptografada = gson.fromJson(decryptedRegionJson, Region.class); // Deserializa
        } catch (Exception e) {
            System.err.println("Erro ao descriptografar ou deserializar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Region getDecryptedJson() {
        return this.regionJsonDescriptografada;
    }
}