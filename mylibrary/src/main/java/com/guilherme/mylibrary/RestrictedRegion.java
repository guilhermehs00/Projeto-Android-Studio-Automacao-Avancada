package com.guilherme.mylibrary;

import android.location.Location;

import java.util.HashMap;
import java.util.Map;

public class RestrictedRegion extends Region{
    private Region mainRegion;
    private boolean restricted;
    private String CmainRegion;
    private String Crestricted;

    public RestrictedRegion(String name, double latitude, double longitude, int user, Region mainRegion, boolean restricted){
        super(name, latitude, longitude, user);
        this.mainRegion = mainRegion;
        this.restricted = restricted;
    }

    public RestrictedRegion(String name, double latitude, double longitude, int user, Region mainRegionDesciptografado, Long timestampDescriptografado, boolean restrictedDescriptografado){
        super(name, latitude, longitude, user, timestampDescriptografado);
        this.mainRegion = mainRegionDesciptografado;
        this.restricted = restrictedDescriptografado;
    }

public RestrictedRegion(String nameJsonCriptografada, String latJsonCriptografada, String longJsonCriptografada, String userJsonCriptografada, String timeJsonCriptografada, String mainRegionJsonCriptografada, String restrictedJsonCriptografada) {
        super(nameJsonCriptografada, latJsonCriptografada, longJsonCriptografada, userJsonCriptografada, timeJsonCriptografada);
        this.CmainRegion = mainRegionJsonCriptografada;
        this.Crestricted = restrictedJsonCriptografada;
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
        regionMap.put("Restricted ", this.Crestricted);
        return regionMap;
    }

    @Override
    public boolean distance(double lat1, double long1, double lat2, double long2){
        float[] distancia = new float[1];
        Location.distanceBetween(lat1, long1, lat2, long2, distancia);
        //System.out.printf("\nCalculando distancia dentro da RestrictedRegion\nDistância: %.2f metros\n", distancia[0]);
        return distancia[0] < 5;
    }

    public Region getMainRegion() {
        return mainRegion;
    }

    public boolean isRestricted() {return restricted;}

    public String getCmainRegion() {
        return CmainRegion;
    }

    public String getCrestricted() {
        return this.Crestricted;
    }

    @Override
    public String toString(){
        return super.toString() + "\n"+ getMainRegion().getName() + "\n" + isRestricted()+ "\n";
    }
}
