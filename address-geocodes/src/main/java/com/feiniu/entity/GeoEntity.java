package com.feiniu.entity;


public class GeoEntity {

    private String longitude;

    private String latitude;

    private String id;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GeoEntity{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
