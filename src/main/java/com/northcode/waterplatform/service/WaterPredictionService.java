package com.northcode.waterplatform.service;

import com.northcode.waterplatform.model.ConstructionSite;
import com.northcode.waterplatform.model.SensorReading;
import com.northcode.waterplatform.model.WaterSource;
import com.northcode.waterplatform.repository.WaterSourceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates candidate groundwater locations around a construction site.
 *
 * The scoring formula is intentionally simple and explainable for a hackathon demo:
 * higher NDVI (vegetation/moisture), higher rainfall, and flatter terrain (lower slope)
 * all increase the probability that groundwater is present nearby.
 *
 * Replace this with a real ML model later if time allows - the API contract
 * (WaterSource list with probabilityScore) stays the same either way.
 */
@Service
public class WaterPredictionService {

    private final WaterSourceRepository waterSourceRepository;

    public WaterPredictionService(WaterSourceRepository waterSourceRepository) {
        this.waterSourceRepository = waterSourceRepository;
    }

    public List<WaterSource> generateCandidates(ConstructionSite site, SensorReading reading) {
        List<WaterSource> candidates = new ArrayList<>();

        // Generate a small set of candidate points in a ring around the site.
        // In a real version these offsets would come from a grid search over satellite tiles.
        double[][] offsets = {
                {0.01, 0.00},
                {-0.01, 0.01},
                {0.00, -0.015},
                {0.015, 0.01},
                {-0.012, -0.008}
        };

        for (double[] offset : offsets) {
            double lat = site.getLatitude() + offset[0];
            double lon = site.getLongitude() + offset[1];

            double score = scoreLocation(reading);
            double distanceKm = haversineKm(site.getLatitude(), site.getLongitude(), lat, lon);

            WaterSource candidate = new WaterSource(
                    lat,
                    lon,
                    score,
                    distanceKm,
                    "ndvi, rainfall, terrain_slope",
                    site
            );
            candidates.add(waterSourceRepository.save(candidate));
        }

        candidates.sort((a, b) -> Double.compare(b.getProbabilityScore(), a.getProbabilityScore()));
        return candidates;
    }

    private double scoreLocation(SensorReading reading) {
        // Normalize each input to a 0-1 contribution, then weight them.
        double ndviScore = clamp((reading.getNdvi() + 1) / 2.0);          // ndvi range -1..1
        double rainfallScore = clamp(reading.getRainfallMm() / 2000.0);   // assume 2000mm/year is very high
        double slopeScore = clamp(1.0 - (reading.getTerrainSlopeDegrees() / 45.0)); // flatter = better, cap at 45deg

        double weighted = (ndviScore * 0.4) + (rainfallScore * 0.4) + (slopeScore * 0.2);
        return Math.round(weighted * 1000.0) / 1000.0;
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }
}
