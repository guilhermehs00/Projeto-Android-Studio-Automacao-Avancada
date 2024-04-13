package com.guilherme.mylibrary;

import android.location.Location;
import java.util.Map;
import java.util.HashMap;

public class Region {
    protected String name;
    protected double latitude;
    protected double longitude;
    protected int user;
    protected long timestamp;
    protected String type;

    public Region() {}

    public Region(String name, double latitude, double longitude, int user, String type) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
        this.timestamp = System.nanoTime();
        this.type = type;
    }

    public Map<String, Object> serialize(String criptografia) {
        Map<String, Object> regionMap = new HashMap<>();
        regionMap.put("RegionCriptografada", criptografia);
        return regionMap;
    }

    public boolean distance(double lat1, double long1, double lat2, double long2){
        float[] results = new float[1];
        Location.distanceBetween(lat1, long1, lat2, long2, results);
        return results[0] < 6;
    }

    public String getName() {return name;}

    public double getLatitude() {return latitude;}

    public double getLongitude() {return longitude;}

    public int getUser() {return user;}

    public long getTimestamp() {return timestamp;}

    public String getType() {return type;}
}
