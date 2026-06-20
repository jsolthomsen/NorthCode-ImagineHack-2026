package com.northcode.waterplatform.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Optimizes water sourcing by comparing local sources vs. transported water.
 * Calculates CO2 savings from using local groundwater instead of transporting water long distances.
 */
@Service
public class WaterTransportOptimizerService {

    private static final Logger logger = LoggerFactory.getLogger(WaterTransportOptimizerService.class);

    // CO2 emission factors (kg CO2 per unit)
    private static final double CO2_PER_KM_TRUCK = 0.12; // kg CO2 per km per 1000L
    private static final double CO2_PER_LITER_PIPELINE = 0.00015; // kg CO2 per liter via pipeline
    private static final double CO2_LOCAL_BOREHOLE = 0.0001; // kg CO2 per liter for local groundwater extraction

    public static class WaterSourceOptimization {
        private String recommendation;
        private double distanceToNearestSourceKm;
        private String nearestSourceLocation;
        private double co2SavingsKgPerLiter;
        private double co2SavingsKgPerDay;
        private String details;

        public WaterSourceOptimization(String recommendation, double distanceToNearestSourceKm,
                                       String nearestSourceLocation, double co2SavingsKgPerLiter,
                                       double co2SavingsKgPerDay, String details) {
            this.recommendation = recommendation;
            this.distanceToNearestSourceKm = distanceToNearestSourceKm;
            this.nearestSourceLocation = nearestSourceLocation;
            this.co2SavingsKgPerLiter = co2SavingsKgPerLiter;
            this.co2SavingsKgPerDay = co2SavingsKgPerDay;
            this.details = details;
        }

        public String getRecommendation() { return recommendation; }
        public double getDistanceToNearestSourceKm() { return distanceToNearestSourceKm; }
        public String getNearestSourceLocation() { return nearestSourceLocation; }
        public double getCo2SavingsKgPerLiter() { return co2SavingsKgPerLiter; }
        public double getCo2SavingsKgPerDay() { return co2SavingsKgPerDay; }
        public String getDetails() { return details; }
    }

    /**
     * Analyze water sourcing optimization for a construction site.
     * @param siteId Site identifier
     * @param distanceToNearestSourceKm Distance to nearest local water source
     * @param currentSupplyMethodType "truck", "pipeline", or "local"
     * @param estimatedDailyWaterLiters Daily water consumption
     * @return Optimization recommendation with CO2 savings
     */
    public WaterSourceOptimization analyzeWaterSource(String siteId, double distanceToNearestSourceKm,
                                                       String currentSupplyMethodType,
                                                       double estimatedDailyWaterLiters) {

        // Calculate current transportation CO2 impact
        double currentCo2PerLiter = calculateCurrentCo2PerLiter(currentSupplyMethodType, distanceToNearestSourceKm);
        double currentDailyCo2 = currentCo2PerLiter * estimatedDailyWaterLiters;

        // Calculate local groundwater CO2 impact
        double localCo2PerLiter = CO2_LOCAL_BOREHOLE;
        double localDailyCo2 = localCo2PerLiter * estimatedDailyWaterLiters;

        // Calculate savings
        double co2SavingsPerLiter = currentCo2PerLiter - localCo2PerLiter;
        double co2SavingsPerDay = currentDailyCo2 - localDailyCo2;

        String recommendation = generateRecommendation(distanceToNearestSourceKm, co2SavingsPerDay);
        String details = String.format(
            "Current method (%s) emits %.4f kg CO2/L; local source emits %.4f kg CO2/L. " +
            "Switching saves %.2f kg CO2/day (%.1f tons/year)",
            currentSupplyMethodType, currentCo2PerLiter, localCo2PerLiter, co2SavingsPerDay, co2SavingsPerDay * 365 / 1000
        );

        logger.info("Water optimization for site {}: {} Savings: {:.2f} kg CO2/day",
            siteId, recommendation, co2SavingsPerDay);

        return new WaterSourceOptimization(
            recommendation,
            distanceToNearestSourceKm,
            formatSourceLocation(distanceToNearestSourceKm),
            co2SavingsPerLiter,
            co2SavingsPerDay,
            details
        );
    }

    private double calculateCurrentCo2PerLiter(String supplyMethod, double distanceKm) {
        return switch (supplyMethod.toLowerCase()) {
            case "truck" -> CO2_PER_KM_TRUCK * (distanceKm / 1000.0); // normalized per liter
            case "pipeline" -> CO2_PER_LITER_PIPELINE * (1 + distanceKm * 0.0001); // increases with distance
            case "local" -> CO2_LOCAL_BOREHOLE;
            default -> CO2_PER_LITER_PIPELINE * (1 + distanceKm * 0.0001);
        };
    }

    private String generateRecommendation(double distanceKm, double dailySavingsKgCo2) {
        if (distanceKm < 5.0 && dailySavingsKgCo2 > 10.0) {
            return "HIGHLY RECOMMENDED: Local water source very close. Use local borehole/well to save significant CO2.";
        } else if (distanceKm < 15.0 && dailySavingsKgCo2 > 5.0) {
            return "RECOMMENDED: Relatively nearby water source. Drilling local borehole would reduce transport emissions.";
        } else if (distanceKm < 50.0 && dailySavingsKgCo2 > 2.0) {
            return "CONSIDER: Local source exists at moderate distance. Evaluate drilling feasibility vs. transport CO2.";
        } else {
            return "CURRENT METHOD ACCEPTABLE: Local source too far or savings minimal. Continue current transport method.";
        }
    }

    private String formatSourceLocation(double distanceKm) {
        if (distanceKm < 1.0) {
            return "On-site or immediate vicinity";
        } else if (distanceKm < 5.0) {
            return String.format("~%.1f km away (very close)", distanceKm);
        } else if (distanceKm < 20.0) {
            return String.format("~%.1f km away (nearby)", distanceKm);
        } else {
            return String.format("~%.1f km away (distant)", distanceKm);
        }
    }
}
