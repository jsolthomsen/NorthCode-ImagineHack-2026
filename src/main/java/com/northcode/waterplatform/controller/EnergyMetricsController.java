package com.northcode.waterplatform.controller;

import com.northcode.waterplatform.security.service.EnergyMonitoringService;
import com.northcode.waterplatform.security.service.WaterTransportOptimizerService;
import org.springframework.web.bind.annotation.*;

/**
 * API endpoints for energy metrics and sustainability recommendations.
 */
@RestController
@RequestMapping("/api/energy-metrics")
public class EnergyMetricsController {

    private final EnergyMonitoringService energyMonitoringService;
    private final WaterTransportOptimizerService waterTransportOptimizer;

    public EnergyMetricsController(EnergyMonitoringService energyMonitoringService,
                                   WaterTransportOptimizerService waterTransportOptimizer) {
        this.energyMonitoringService = energyMonitoringService;
        this.waterTransportOptimizer = waterTransportOptimizer;
    }

    /**
     * GET /api/energy-metrics
     * Returns energy consumption report and optimization recommendations.
     */
    @GetMapping
    public EnergyMetricsResponse getEnergyMetrics() {
        EnergyMonitoringService.EnergyReport report = energyMonitoringService.getEnergyReport();
        String recommendation = energyMonitoringService.getOptimizationRecommendation();

        return new EnergyMetricsResponse(
            report.getTotalEnergyWh(),
            report.getUpTimeHours(),
            report.getAveragePowerW(),
            recommendation,
            report.getReportTime().toString()
        );
    }

    /**
     * GET /api/energy-metrics/water-optimization
     * Analyzes water sourcing and recommends local sources to reduce transport CO2.
     *
     * @param siteId Site identifier
     * @param distanceToNearestSourceKm Distance to nearest water source (km)
     * @param currentSupplyMethod Current water supply method (truck, pipeline, local)
     * @param estimatedDailyWaterLiters Daily water consumption (liters)
     */
    @GetMapping("/water-optimization")
    public WaterTransportOptimizerService.WaterSourceOptimization analyzeWaterSource(
            @RequestParam String siteId,
            @RequestParam double distanceToNearestSourceKm,
            @RequestParam String currentSupplyMethod,
            @RequestParam(defaultValue = "50000") double estimatedDailyWaterLiters) {

        return waterTransportOptimizer.analyzeWaterSource(
            siteId,
            distanceToNearestSourceKm,
            currentSupplyMethod,
            estimatedDailyWaterLiters
        );
    }

    /**
     * Response model for energy metrics
     */
    public static class EnergyMetricsResponse {
        private double totalEnergyWh;
        private double upTimeHours;
        private double averagePowerW;
        private String recommendation;
        private String reportTime;

        public EnergyMetricsResponse(double totalEnergyWh, double upTimeHours, double averagePowerW,
                                     String recommendation, String reportTime) {
            this.totalEnergyWh = totalEnergyWh;
            this.upTimeHours = upTimeHours;
            this.averagePowerW = averagePowerW;
            this.recommendation = recommendation;
            this.reportTime = reportTime;
        }

        public double getTotalEnergyWh() { return totalEnergyWh; }
        public double getUpTimeHours() { return upTimeHours; }
        public double getAveragePowerW() { return averagePowerW; }
        public String getRecommendation() { return recommendation; }
        public String getReportTime() { return reportTime; }
    }
}
