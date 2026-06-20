package com.northcode.waterplatform.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Monitors and optimizes energy consumption of application operations.
 * Critical for construction sites in Africa with limited power (solar, generators).
 */
@Service
public class EnergyMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(EnergyMonitoringService.class);

    // Energy cost estimates in microwatt-hours (µWh)
    private static final double API_CALL_UWH = 0.5;
    private static final double DB_QUERY_UWH = 2.0;
    private static final double WATER_PREDICTION_UWH = 50.0;
    private static final double IMAGE_PROCESSING_UWH = 30.0;

    private double totalEnergyConsumption = 0.0; // in µWh
    private long startTime = System.currentTimeMillis();

    /**
     * Log an API request's energy cost
     */
    public void logApiCall(String endpoint, long responseTimeMs) {
        double cost = API_CALL_UWH + (responseTimeMs * 0.01);
        totalEnergyConsumption += cost;
        logger.debug("API Call [" + endpoint + "]: " + String.format("%.2f", cost) + " µWh");
    }

    /**
     * Log a database query's energy cost
     */
    public void logDatabaseQuery(String queryType, long executionTimeMs, int rowsAffected) {
        double cost = DB_QUERY_UWH + (executionTimeMs * 0.02) + (rowsAffected * 0.1);
        totalEnergyConsumption += cost;
        logger.debug("DB Query [" + queryType + "]: " + String.format("%.2f", cost) + " µWh");
    }

    /**
     * Log water prediction model computation (expensive operation)
     */
    public void logWaterPrediction(String siteId, long computationTimeMs, int dataPointsProcessed) {
        double cost = WATER_PREDICTION_UWH + (computationTimeMs * 0.05) + (dataPointsProcessed * 0.5);
        totalEnergyConsumption += cost;
        logger.info("Water Prediction [Site: " + siteId + "]: " + String.format("%.2f", cost) + " µWh");
    }

    /**
     * Log satellite/sensor image processing
     */
    public void logImageProcessing(String siteId, long processingTimeMs, int imageCount) {
        double cost = IMAGE_PROCESSING_UWH + (processingTimeMs * 0.03) + (imageCount * 5.0);
        totalEnergyConsumption += cost;
        logger.info("Image Processing [Site: " + siteId + "]: " + String.format("%.2f", cost) + " µWh");
    }

    /**
     * Get current energy consumption report
     */
    public EnergyReport getEnergyReport() {
        long upTimeMs = System.currentTimeMillis() - startTime;
        double upTimeHours = upTimeMs / (1000.0 * 3600.0);
        double totalWh = totalEnergyConsumption / 1_000_000.0;
        double avgPowerW = totalWh / Math.max(upTimeHours, 0.001);

        return new EnergyReport(totalWh, upTimeHours, avgPowerW, LocalDateTime.now());
    }

    /**
     * Check if consumption is within daily energy budget
     * (important for solar-powered or off-grid sites)
     */
    public boolean isWithinBudget(double maxEnergyWh) {
        return (totalEnergyConsumption / 1_000_000.0) <= maxEnergyWh;
    }

    /**
     * Get optimization recommendation based on current consumption
     */
    public String getOptimizationRecommendation() {
        double totalWh = totalEnergyConsumption / 1_000_000.0;

        if (totalWh > 100.0) {
            return "HIGH energy consumption. Batch water predictions and cache results to reduce computation frequency.";
        } else if (totalWh > 50.0) {
            return "MODERATE energy usage. Optimize database queries and image processing batch sizes.";
        } else if (totalWh > 10.0) {
            return "Normal energy consumption. Continue monitoring.";
        }
        return "Energy consumption is optimal.";
    }

    /**
     * Reset energy counter (for daily tracking)
     */
    public void resetDailyCounter() {
        totalEnergyConsumption = 0.0;
        startTime = System.currentTimeMillis();
        logger.info("Daily energy counter reset");
    }

    public static class EnergyReport {
        private double totalEnergyWh;
        private double upTimeHours;
        private double averagePowerW;
        private LocalDateTime reportTime;

        public EnergyReport(double totalEnergyWh, double upTimeHours, double averagePowerW, LocalDateTime reportTime) {
            this.totalEnergyWh = totalEnergyWh;
            this.upTimeHours = upTimeHours;
            this.averagePowerW = averagePowerW;
            this.reportTime = reportTime;
        }

        public double getTotalEnergyWh() {
            return totalEnergyWh;
        }

        public double getUpTimeHours() {
            return upTimeHours;
        }

        public double getAveragePowerW() {
            return averagePowerW;
        }

        public LocalDateTime getReportTime() {
            return reportTime;
        }

        @Override
        public String toString() {
            return String.format("Energy Report [Time: %s | Total: %.2f Wh | Runtime: %.2f h | Avg Power: %.2f W]",
                    reportTime, totalEnergyWh, upTimeHours, averagePowerW);
        }
    }
}
