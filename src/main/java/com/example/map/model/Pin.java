package com.example.map.model;

public class Pin {
    private int id;
    private double lat;
    private double lng;
    private String status;
    private String personName;

    public Pin(int id, double lat, double lng, String status, String personName) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
        this.personName = personName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
