package com.northcode.waterplatform.service;

import com.northcode.waterplatform.dto.CreateSiteRequest;
import com.northcode.waterplatform.dto.SensorUploadRequest;
import com.northcode.waterplatform.model.ConstructionSite;
import com.northcode.waterplatform.model.SensorReading;
import com.northcode.waterplatform.model.WaterSource;
import com.northcode.waterplatform.repository.ConstructionSiteRepository;
import com.northcode.waterplatform.repository.SensorReadingRepository;
import com.northcode.waterplatform.repository.WaterSourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SiteService {

    private final ConstructionSiteRepository siteRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final WaterSourceRepository waterSourceRepository;
    private final WaterPredictionService predictionService;

    public SiteService(ConstructionSiteRepository siteRepository,
                        SensorReadingRepository sensorReadingRepository,
                        WaterSourceRepository waterSourceRepository,
                        WaterPredictionService predictionService) {
        this.siteRepository = siteRepository;
        this.sensorReadingRepository = sensorReadingRepository;
        this.waterSourceRepository = waterSourceRepository;
        this.predictionService = predictionService;
    }

    public ConstructionSite createSite(CreateSiteRequest request) {
        ConstructionSite site = new ConstructionSite(
                request.getName(),
                request.getCountry(),
                request.getLatitude(),
                request.getLongitude()
        );
        return siteRepository.save(site);
    }

    public List<ConstructionSite> getAllSites() {
        return siteRepository.findAll();
    }

    public ConstructionSite getSite(Long id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Site not found: " + id));
    }

    /**
     * Accepts a sensor/satellite reading for a site and immediately generates
     * a ranked list of candidate groundwater locations from it.
     */
    public List<WaterSource> uploadSensorData(SensorUploadRequest request) {
        ConstructionSite site = getSite(request.getSiteId());

        SensorReading reading = new SensorReading(
                site,
                request.getNdvi(),
                request.getRainfallMm(),
                request.getTerrainSlopeDegrees()
        );
        sensorReadingRepository.save(reading);

        return predictionService.generateCandidates(site, reading);
    }

    public List<WaterSource> getWaterSources(Long siteId) {
        return waterSourceRepository.findBySiteIdOrderByProbabilityScoreDesc(siteId);
    }
}
