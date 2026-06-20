package com.northcode.waterplatform.service;

import com.northcode.waterplatform.model.ConstructionSite;
import com.northcode.waterplatform.model.WaterQuality;
import com.northcode.waterplatform.repository.WaterQualityRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assesses whether water at a construction site is likely safe to use,
 * based on the team's original pH + air quality logic.
 */
@Service
public class WaterQualityService {

    private final WaterQualityRepository waterQualityRepository;
    private final SoilDataService soilDataService;
    private final RestTemplate restTemplate = new RestTemplate();

    // Replace with your own OpenWeatherMap key
    private static final String OPENWEATHER_API_KEY = "YOUR_API_KEY_HERE";

    public WaterQualityService(WaterQualityRepository waterQualityRepository, SoilDataService soilDataService) {
        this.waterQualityRepository = waterQualityRepository;
        this.soilDataService = soilDataService;
    }

    public WaterQuality assessWaterQuality(ConstructionSite site) {
        double latitude = site.getLatitude();
        double longitude = site.getLongitude();

        Map<String, Double> airQualityData = fetchAirQualityData(latitude, longitude);

        // pH now comes from the shared SoilDataService (same SoilGrids call used for water source prediction)
        SoilDataService.SoilProperties soil = soilDataService.fetchSoilProperties(latitude, longitude);
        double pH = soil.isRealData() ? soil.pH() : estimatePHFromAirQuality(airQualityData);

        double turbidity = calculateTurbidityBasedOnAirQuality(airQualityData);

        Map<String, Double> parameters = new HashMap<>();
        parameters.put("pH", pH);
        parameters.put("turbidity", turbidity);

        double waterQualityIndex = calculateWaterQualityIndex(parameters);

        String status = buildStatus(pH, turbidity, airQualityData);

        WaterQuality waterQuality = new WaterQuality(site, pH, turbidity, waterQualityIndex, status);
        return waterQualityRepository.save(waterQuality);
    }

    public List<WaterQuality> getQualityHistory(Long siteId) {
        return waterQualityRepository.findBySiteId(siteId);
    }

    private String buildStatus(double pH, double turbidity, Map<String, Double> airQualityData) {
        StringBuilder reasons = new StringBuilder();

        if (turbidity > 5.0) {
            reasons.append("High turbidity level (").append(turbidity).append(" NTU), exceeds safe limit of 5.0 NTU. ");
        }
        if (pH < 6.5 || pH > 8.5) {
            reasons.append("pH level (").append(pH).append(") is outside the safe range of 6.5 to 8.5. ");
        }
        if (airQualityData.getOrDefault("pm2_5", 0.0) > 25.0) {
            reasons.append("PM2.5 level is higher than the safe limit of 25.0 µg/m³. ");
        }
        if (airQualityData.getOrDefault("no2", 0.0) > 20.0) {
            reasons.append("NO2 level exceeds the safe limit of 20.0 µg/m³. ");
        }
        if (airQualityData.getOrDefault("nh3", 0.0) > 1.0) {
            reasons.append("Ammonia (NH3) level is significantly above the safe limit of 1.0 mg/L. ");
        }

        return reasons.isEmpty() ? "Clean and safe for drinking." : "Not safe for drinking. Reasons: " + reasons;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Double> fetchAirQualityData(double latitude, double longitude) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/air_pollution?lat=%f&lon=%f&appid=%s",
                latitude, longitude, OPENWEATHER_API_KEY);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("list")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("list");
                if (!list.isEmpty()) {
                    Map<String, Object> components = (Map<String, Object>) list.get(0).get("components");
                    if (components != null) {
                        Map<String, Double> result = new HashMap<>();
                        for (Map.Entry<String, Object> entry : components.entrySet()) {
                            try {
                                result.put(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch air quality data: " + e.getMessage());
        }
        return new HashMap<>();
    }

    public double estimatePHFromAirQuality(Map<String, Double> airQualityData) {
        double pH = 7.0;
        double no2 = airQualityData.getOrDefault("no2", 0.0);
        double so2 = airQualityData.getOrDefault("so2", 0.0);
        double nh3 = airQualityData.getOrDefault("nh3", 0.0);

        pH -= (no2 * 0.02) + (so2 * 0.03);
        pH += nh3 * 0.01;

        return Math.max(0, Math.min(14, pH));
    }

    public double calculateTurbidityBasedOnAirQuality(Map<String, Double> airQualityData) {
        return airQualityData.containsKey("pm10") ? airQualityData.get("pm10") / 10.0 : 0.0;
    }

    public double calculateWaterQualityIndex(Map<String, Double> parameters) {
        double pH = parameters.getOrDefault("pH", 7.0);
        double turbidity = parameters.getOrDefault("turbidity", 0.0);
        return (14 - Math.abs(7 - pH)) + (10 - turbidity);
    }
}
