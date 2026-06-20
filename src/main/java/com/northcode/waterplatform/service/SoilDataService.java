package com.northcode.waterplatform.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fetches real groundwater-relevant soil properties from the SoilGrids REST API
 * (https://rest.isric.org). No API key required.
 *
 * Properties fetched:
 * - phh2o: soil pH in water (affects water quality/usability)
 * - ocd: organic carbon density (proxy for moisture retention / vegetation)
 * - cec: cation exchange capacity (soil's ability to hold water and nutrients)
 */
@Service
public class SoilDataService {

    private final RestTemplate restTemplate = new RestTemplate();

    public SoilProperties fetchSoilProperties(double latitude, double longitude) {
        String url = String.format(Locale.US,
                "https://rest.isric.org/soilgrids/v2.0/properties/query?lon=%.6f&lat=%.6f&property=phh2o,ocd,cec&depth=0-5cm&value=mean",
                longitude, latitude);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("properties")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> properties = (Map<String, Object>) response.get("properties");

                Double pH = extractValue(properties, "phh2o");
                Double ocd = extractValue(properties, "ocd");
                Double cec = extractValue(properties, "cec");

                if (pH != null && ocd != null && cec != null) {
                    // SoilGrids returns scaled integers - divide by conversion factor per property docs.
                    // phh2o is x10 (e.g. 65 = pH 6.5), ocd and cec are returned in their native scaled units.
                    return new SoilProperties(pH / 10.0, ocd, cec, true);
                }
            }
            System.err.println("SoilGrids data missing or malformed for lat=" + latitude + " lon=" + longitude);
        } catch (Exception e) {
            System.err.println("Failed to fetch SoilGrids data: " + e.getMessage());
        }

        // Fallback - lets the prediction service keep working (e.g. offline at the venue, rate limited)
        return fallback();
    }

    @SuppressWarnings("unchecked")
    private Double extractValue(Map<String, Object> properties, String propertyKey) {
        try {
            Object layersObj = properties.get("layers");
            if (!(layersObj instanceof List<?> layers)) return null;

            for (Object layerObj : layers) {
                Map<String, Object> layer = (Map<String, Object>) layerObj;
                if (propertyKey.equals(layer.get("name"))) {
                    List<Map<String, Object>> depths = (List<Map<String, Object>>) layer.get("depths");
                    if (depths != null && !depths.isEmpty()) {
                        Map<String, Object> values = (Map<String, Object>) depths.get(0).get("values");
                        Object mean = values.get("mean");
                        if (mean != null) {
                            return Double.parseDouble(mean.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to extract " + propertyKey + ": " + e.getMessage());
        }
        return null;
    }

    private SoilProperties fallback() {
        // Reasonable defaults so the demo never crashes if the API is unreachable at the venue
        return new SoilProperties(6.8, 35.0, 18.0, false);
    }

    public record SoilProperties(double pH, double organicCarbonDensity, double cationExchangeCapacity, boolean isRealData) {
    }
}
