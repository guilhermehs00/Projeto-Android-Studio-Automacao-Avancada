package com.guilherme.mylibrary;

import android.location.Location;

import java.util.HashMap;
import java.util.Map;

public class SubRegion extends Region{
    private Region mainRegion;

    public SubRegion(String name, double latitude, double longitude, int user, Region mainRegion, String type){
        super(name, latitude, longitude, user, type);
        this.mainRegion = mainRegion;
    }

    @Override
    public Map<String, Object> serialize(String criptografia) {
        Map<String, Object> subRegionMap = new HashMap<>();
        subRegionMap.put("RegionCriptografada", criptografia);
        return subRegionMap;
    }

    @Override
    public boolean distance(double lat1, double long1, double lat2, double long2){
        float[] distancia = new float[1];
        Location.distanceBetween(lat1, long1, lat2, long2, distancia);
        return distancia[0] < 3;
    }

    public Region getMainRegion() {
        return mainRegion;
    }
}
