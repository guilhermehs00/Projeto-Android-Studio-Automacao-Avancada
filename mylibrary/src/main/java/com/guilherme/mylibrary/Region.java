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

    protected String Cname;
    protected String Clatitude;
    protected String Clongitude;
    protected String Cuser;
    protected String Ctimestamp;

    public Region() {}

    //Contrutor para criar uma Região Principal
    public Region(String name, double latitude, double longitude, int user) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
        this.timestamp = System.nanoTime();
    }

    //Contrutor para criar uma Região Principal Descriptografada
    public Region(String Dname, double Dlatitude, double Dlongitude, int Duser, long timestampDescriptografado) {
        this.name = Dname;
        this.latitude = Dlatitude;
        this.longitude = Dlongitude;
        this.user = Duser;
        this.timestamp = timestampDescriptografado;
    }

    //Contrutor para criar uma Região Principal Criptografada
    public Region(String Cname, String Clatitude, String Clongitude, String Cuser, String Ctimestamp) {
        this.Cname = Cname;
        this.Clatitude = Clatitude;
        this.Clongitude = Clongitude;
        this.Cuser = Cuser;
        this.Ctimestamp = Ctimestamp;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> regionMap = new HashMap<>();
        regionMap.put("Nome ", getCname());
        regionMap.put("Latitude ", getClatitude());
        regionMap.put("Longitude ", getClongitude());
        regionMap.put("User ", getCuser());
        regionMap.put("Timestamp ", getCtimestamp());
        return regionMap;
    }

    public boolean distance(double lat1, double long1, double lat2, double long2){
        float[] results = new float[1];
        Location.distanceBetween(lat1, long1, lat2, long2, results);
        //System.out.printf("\nCalculando distancia dentro da Region\nDistância: %.2f metros\n", results[0]);
        return results[0] < 30;
    }

    public String getName() {return name;}

    public double getLatitude() {return latitude;}

    public double getLongitude() {return longitude;}

    public int getUser() {return user;}

    public long getTimestamp() {return timestamp;}

    public String getCname(){
        return Cname;
    }

    public String getClatitude() {
        return Clatitude;
    }

    public String getClongitude() {
        return Clongitude;
    }

    public String getCuser() {
        return Cuser;
    }

    public String getCtimestamp() {
        return Ctimestamp;
    }

    @Override
    public String toString(){
        return "\n"+getName() + "\n" + getLatitude() + "\n" + getLatitude() + "\n" + getUser() +
                "\n" + getTimestamp() + "\n\n";
    }
}
