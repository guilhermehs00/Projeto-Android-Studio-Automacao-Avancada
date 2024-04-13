package com.guilherme.mylibrary;

import com.google.gson.Gson;

public class CriptografarDados extends Thread {
    private Region region;
    private volatile String regionJsonCriptografada = "NULL";  // Garantir visibilidade entre threads
    private static final Gson gson = new Gson();  // Static para reutilização

    public CriptografarDados(Region region) {
        this.region = region;
    }

    @Override
    public void run() {
        try {
            String regionJson = gson.toJson(region); // Converte o objeto Region em uma string JSON
            this.regionJsonCriptografada = Cryptography.encrypt(regionJson); // Criptografa o JSON
        } catch (Exception e) {
            System.err.println("\n\nErro ao criptografar os dados: " + e.getMessage() + "\n\n");
            e.printStackTrace();
        }
    }

    public String getEncryptedJson() {return this.regionJsonCriptografada;}
}
