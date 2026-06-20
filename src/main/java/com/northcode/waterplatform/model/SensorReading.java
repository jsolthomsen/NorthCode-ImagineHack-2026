package com.northcode.waterplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings")
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private ConstructionSite site;

    // Normalized Difference Vegetation Index, -1.0 to 1.0. Higher often correlates with moisture.
    private double ndvi;

    // average annual rainfall in mm
    private double rainfallMm;

    // terrain slope in degrees - flatter areas retain groundwater better
    private double terrainSlopeDegrees;

    private LocalDateTime uploadedAt;

    public SensorReading() {
    }

    public SensorReading(ConstructionSite site, double ndvi, double rainfallMm, double terrainSlopeDegrees) {
        this.site = site;
        this.ndvi = ndvi;
        this.rainfallMm = rainfallMm;
        this.terrainSlopeDegrees = terrainSlopeDegrees;
        this.uploadedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConstructionSite getSite() {
        return site;
    }

    public void setSite(ConstructionSite site) {
        this.site = site;
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

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
