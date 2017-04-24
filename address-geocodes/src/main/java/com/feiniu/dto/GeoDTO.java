package com.feiniu.dto;


public class GeoDTO {
    private String longitude;

    private String latitude;

    private String memGuid;

    private String areaCode;

    public GeoDTO(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

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

    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String toString() {
        return "GeoDTO{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", memGuid='" + memGuid + '\'' +
                ", areaCode='" + areaCode + '\'' +
                '}';
    }
}
