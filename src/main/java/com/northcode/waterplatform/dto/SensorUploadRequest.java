package com.northcode.waterplatform.dto;

public class SensorUploadRequest {

    private Long siteId;
    private double ndvi;
    private double rainfallMm;
    private double terrainSlopeDegrees;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public double getNdvi() {
        return ndvi;
    }

    public void setNdvi(double ndvi) {
        this.ndvi = ndvi;
    }

    public double getRainfallMm() {
        return rainfallMm;
    }

    public void setRainfallMm(double rainfallMm) {
        this.rainfallMm = rainfallMm;
    }

    public double getTerrainSlopeDegrees() {
        return terrainSlopeDegrees;
    }

    public void setTerrainSlopeDegrees(double terrainSlopeDegrees) {
        this.terrainSlopeDegrees = terrainSlopeDegrees;
    }
}
