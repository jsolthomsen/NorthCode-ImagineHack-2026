package com.northcode.waterplatform.model;

import jakarta.persistence.*;

@Entity
@Table(name = "water_sources")
public class WaterSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;
    private double longitude;

    // 0.0 - 1.0 probability that groundwater exists at this location
    private double probabilityScore;

    // distance in km from the construction site to this water source
    private double distanceKm;

    // which inputs contributed to the score, e.g. "ndvi, rainfall, terrain_slope"
    private String dataSourcesUsed;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private ConstructionSite site;

    public WaterSource() {
    }

    public WaterSource(double latitude, double longitude, double probabilityScore,
                        double distanceKm, String dataSourcesUsed, ConstructionSite site) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.probabilityScore = probabilityScore;
        this.distanceKm = distanceKm;
        this.dataSourcesUsed = dataSourcesUsed;
        this.site = site;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getProbabilityScore() {
        return probabilityScore;
    }

    public void setProbabilityScore(double probabilityScore) {
        this.probabilityScore = probabilityScore;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getDataSourcesUsed() {
        return dataSourcesUsed;
    }

    public void setDataSourcesUsed(String dataSourcesUsed) {
        this.dataSourcesUsed = dataSourcesUsed;
    }

    public ConstructionSite getSite() {
        return site;
    }

    public void setSite(ConstructionSite site) {
        this.site = site;
    }
}
