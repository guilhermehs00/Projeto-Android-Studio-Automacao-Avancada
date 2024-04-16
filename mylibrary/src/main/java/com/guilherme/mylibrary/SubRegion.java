package com.guilherme.mylibrary;

import android.location.Location;

import java.util.HashMap;
import java.util.Map;

public class SubRegion extends Region{
    private Region mainRegion;
    private String CmainRegion;

    //Contrutor para criar uma SubRegião
    public SubRegion(String name, double latitude, double longitude, int user, Region mainRegion){
        super(name, latitude, longitude, user);
        this.mainRegion = mainRegion;
    }

    //Contrutor para criar uma SubRegião Descriptograda
    public SubRegion(String name, double latitude, double longitude, int user, Region mainRegionDesciptografado, long timestampDescriptografado){
        super(name, latitude, longitude, user, timestampDescriptografado);
        this.mainRegion = mainRegionDesciptografado;
    }

    //Contrutor para criar uma SubRegião Criptograda
    public SubRegion(String nameJsonCriptografada, String latJsonCriptografada, String longJsonCriptografada, String userJsonCriptografada, String timeJsonCriptografada, String mainRegionJsonCriptografada) {
        super(nameJsonCriptografada, latJsonCriptografada, longJsonCriptografada, userJsonCriptografada, timeJsonCriptografada);
        this.CmainRegion = mainRegionJsonCriptografada;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> regionMap = new HashMap<>();
        regionMap.put("Nome ", this.Cname);
        regionMap.put("Latitude ", this.Clatitude);
        regionMap.put("Longitude ", this.Clongitude);
        regionMap.put("User ", this.Cuser);
        regionMap.put("Timestamp ", this.Ctimestamp);
        regionMap.put("Região principal ", this.CmainRegion);
        return regionMap;
    }

    @Override
    public boolean distance(double lat1, double long1, double lat2, double long2){
        System.out.println("\nCalculando distancia dentro da SubRegion\n");
        float[] distancia = new float[1];
        Location.distanceBetween(lat1, long1, lat2, long2, distancia);
        return distancia[0] < 5;
    }

    public Region getMainRegion() {
        return mainRegion;
    }

    public String getCmainRegion() {
        return CmainRegion;
    }

    @Override
    public String toString(){
        return super.toString() + "\n"+ getMainRegion().getName() + "\n";
    }
}
