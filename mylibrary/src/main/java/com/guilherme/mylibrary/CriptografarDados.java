package com.guilherme.mylibrary;

import com.google.gson.Gson;

public class CriptografarDados extends Thread {
    private static final Gson gson = new Gson();
    private Region region;
    private SubRegion Sregion;
    private RestrictedRegion Rregion;

    private Region regionJsonCriptografada = null;
    private SubRegion SregionJsonCriptografada = null;
    private RestrictedRegion RregionJsonCriptografada = null;

    private String nameJsonCriptografada;
    private String latJsonCriptografada;
    private String longJsonCriptografada;
    private String userJsonCriptografada;
    private String timeJsonCriptografada;
    private String mainRegionJsonCriptografada;
    private String restrictedJsonCriptografada;

    public CriptografarDados(Region region) {
        this.region = region;
        this.Sregion = null;
        this.Rregion = null;
    }
    public CriptografarDados(SubRegion Sregion) {
        this.Sregion = Sregion;
        this.region = null;
        this.Rregion = null;
    }
    public CriptografarDados(RestrictedRegion Rregion) {
        this.Rregion = Rregion;
        this.region = null;
        this.Sregion = null;
    }

    @Override
    public void run() {
        try {
            if (region != null){
                encryptMain(region);

                this.regionJsonCriptografada = new Region(
                        nameJsonCriptografada,
                        latJsonCriptografada,
                        longJsonCriptografada,
                        userJsonCriptografada,
                        timeJsonCriptografada
                );

            } else if (Sregion != null) {
                encryptMain(Sregion);

                String mainRegionJson = gson.toJson(Sregion.getMainRegion());
                this.mainRegionJsonCriptografada = Cryptography.encrypt(mainRegionJson);

                this.SregionJsonCriptografada = new SubRegion(
                        nameJsonCriptografada,
                        latJsonCriptografada,
                        longJsonCriptografada,
                        userJsonCriptografada,
                        timeJsonCriptografada,
                        mainRegionJsonCriptografada
                );

            }else{
                encryptMain(Rregion);

                String mainRegionJson = gson.toJson(Rregion.getMainRegion());
                this.mainRegionJsonCriptografada = Cryptography.encrypt(mainRegionJson);

                String restrictedJson = gson.toJson(Rregion.isRestricted());
                this.restrictedJsonCriptografada = Cryptography.encrypt(restrictedJson);

                this.RregionJsonCriptografada = new RestrictedRegion(
                        nameJsonCriptografada,
                        latJsonCriptografada,
                        longJsonCriptografada,
                        userJsonCriptografada,
                        timeJsonCriptografada,
                        mainRegionJsonCriptografada,
                        restrictedJsonCriptografada
                );
            }

        } catch (Exception e) {
            System.err.println("\n\nErro ao criptografar os dados: " + e.getMessage() + "\n\n");
            e.printStackTrace();
        }
    }
    private void encryptMain(Region r){
        try{
            this.nameJsonCriptografada = Cryptography.encrypt(r.getName());

            String latJson = gson.toJson(r.getLatitude());
            this.latJsonCriptografada = Cryptography.encrypt(latJson);

            String longJson = gson.toJson(r.getLongitude());
            this.longJsonCriptografada = Cryptography.encrypt(longJson);

            String userJson = gson.toJson(r.getUser());
            this.userJsonCriptografada = Cryptography.encrypt(userJson);

            String timeJson = gson.toJson(r.getTimestamp());
            this.timeJsonCriptografada = Cryptography.encrypt(timeJson);

        }catch (Exception e) {
            System.err.println("\n\nErro ao criptografar os dados: " + e.getMessage() + "\n\n");
            e.printStackTrace();
        }

    }
    public Region getRegionEncryptedJson() {return this.regionJsonCriptografada;}
    public SubRegion getSRegionEncryptedJson() {return this.SregionJsonCriptografada;}
    public RestrictedRegion getRRegionEncryptedJson() {return this.RregionJsonCriptografada;}
}
