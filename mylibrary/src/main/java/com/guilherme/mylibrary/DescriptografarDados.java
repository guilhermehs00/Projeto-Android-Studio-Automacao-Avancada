package com.guilherme.mylibrary;

import com.google.gson.Gson;

public class DescriptografarDados extends Thread {
    private static final Gson gson = new Gson();  // Static para reutilização
    private Region regionCriptografada;
    private SubRegion SregionCriptografada;
    private RestrictedRegion RregionCriptografada;

    private String nameDescriptografado;
    private double latDescriptografado;
    private double longDescriptografado;
    private int userDescriptografado;
    private long timeDescriptografado;
    private Region mainRegionDescriptografado;
    private boolean restrictedJDescriptografado;

    private volatile Region regionJsonDescriptografada;  // Volatile para visibilidade entre threads
    private volatile SubRegion SregionJsonDescriptografada;
    private volatile RestrictedRegion RregionJsonDescriptografada;

    // Contrutor para decriptografar Região
    public DescriptografarDados(Region regionCriptografada) {
        this.regionCriptografada = regionCriptografada;
        this.SregionCriptografada = null;
        this.RregionCriptografada = null;
    }

    // Contrutor para decriptografar SubRegião
    public DescriptografarDados(SubRegion SregionCriptografada) {
        this.regionCriptografada = null;
        this.SregionCriptografada = SregionCriptografada;
        this.RregionCriptografada = null;
    }

    // Contrutor para decriptografar Região Restrita
    public DescriptografarDados(RestrictedRegion RregionCriptografada) {
        this.regionCriptografada = null;
        this.SregionCriptografada = null;
        this.RregionCriptografada = RregionCriptografada;

    }

    @Override
    public void run() {
        try {
            if (regionCriptografada != null) {

                DescryptMain(regionCriptografada); // Descriptografa atributos principais

                // Cria um novo objeto Region com os valores descriptografados
                regionJsonDescriptografada = new Region(
                        nameDescriptografado,
                        latDescriptografado,
                        longDescriptografado,
                        userDescriptografado,
                        timeDescriptografado
                );

            }else if(SregionCriptografada != null){
                DescryptMain(SregionCriptografada); // Descriptografa atributos principais

                String decryptedMainRegionJson = Cryptography.decrypt(SregionCriptografada.getCmainRegion()); // Descriptografa
                this.mainRegionDescriptografado = gson.fromJson(decryptedMainRegionJson, Region.class); // Deserializa

                // Cria um novo objeto SubRegion com os valores descriptografados
                this.SregionJsonDescriptografada = new SubRegion(
                        nameDescriptografado,
                        latDescriptografado,
                        longDescriptografado,
                        userDescriptografado,
                        mainRegionDescriptografado,
                        timeDescriptografado
                );

            }else{
                DescryptMain(RregionCriptografada); // Descriptografa atributos principais

                String decryptedMainRegionJson = Cryptography.decrypt(RregionCriptografada.getCmainRegion()); // Descriptografa
                this.mainRegionDescriptografado = gson.fromJson(decryptedMainRegionJson, Region.class); // Deserializa

                String decryptedRestrictedJson = Cryptography.decrypt(RregionCriptografada.getCrestricted()); // Descriptografa
                this.restrictedJDescriptografado = gson.fromJson(decryptedRestrictedJson, Boolean.class); // Deserializa

                // Cria um novo objeto RestrictedRegion com os valores descriptografados
                this.RregionJsonDescriptografada = new RestrictedRegion(
                        nameDescriptografado,
                        latDescriptografado,
                        longDescriptografado,
                        userDescriptografado,
                        mainRegionDescriptografado,
                        timeDescriptografado,
                        restrictedJDescriptografado
                );
            }

        } catch (Exception e) {
            System.err.println("Erro ao descriptografar ou deserializar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Descriptografa os atributos da super classe
    private void DescryptMain(Region r){
        try{
            // Descriptografa e armazena cada campo
            this.nameDescriptografado = Cryptography.decrypt(r.getCname());
            this.latDescriptografado = Double.parseDouble(Cryptography.decrypt(r.getClatitude()));
            this.longDescriptografado = Double.parseDouble(Cryptography.decrypt(r.getClongitude()));
            this.userDescriptografado = Integer.parseInt(Cryptography.decrypt(r.getCuser()));
            this.timeDescriptografado = Long.parseLong(Cryptography.decrypt(r.getCtimestamp()));
        }catch (Exception e) {
            System.err.println("Erro ao descriptografar ou deserializar dados: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public Region getRegionDesCriptografada() {
        return this.regionJsonDescriptografada;
    }
    public SubRegion getSubRegionDesCriptografada() {
        return this.SregionJsonDescriptografada;
    }
    public RestrictedRegion getRestRegionDesCriptografada() {return this.RregionJsonDescriptografada;}
}
