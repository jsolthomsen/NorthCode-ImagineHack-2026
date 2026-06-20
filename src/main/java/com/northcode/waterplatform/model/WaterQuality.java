package com.northcode.waterplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_quality")
public class WaterQuality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private ConstructionSite site;

    private double pH;
    private double turbidity;
    private double waterQualityIndex;
    private String status; // "Clean and safe for drinking." or "Not safe for drinking. Reasons: ..."

    private LocalDateTime timestamp;

    public WaterQuality() {
    }

    public WaterQuality(ConstructionSite site, double pH, double turbidity, double waterQualityIndex, String status) {
        this.site = site;
        this.pH = pH;
        this.turbidity = turbidity;
        this.waterQualityIndex = waterQualityIndex;
        this.status = status;
        this.timestamp = LocalDateTime.now();
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

    public double getPH() {
        return pH;
    }

    public void setPH(double pH) {
        this.pH = pH;
    }

    public double getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(double turbidity) {
        this.turbidity = turbidity;
    }

    public double getWaterQualityIndex() {
        return waterQualityIndex;
    }

    public void setWaterQualityIndex(double waterQualityIndex) {
        this.waterQualityIndex = waterQualityIndex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
