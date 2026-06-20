package com.northcode.waterplatform.controller;

import com.northcode.waterplatform.dto.CreateSiteRequest;
import com.northcode.waterplatform.dto.SensorUploadRequest;
import com.northcode.waterplatform.model.ConstructionSite;
import com.northcode.waterplatform.model.WaterSource;
import com.northcode.waterplatform.service.SiteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // open for the hackathon demo - tighten this later
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @PostMapping("/sites")
    @ResponseStatus(HttpStatus.CREATED)
    public ConstructionSite createSite(@Valid @RequestBody CreateSiteRequest request) {
        return siteService.createSite(request);
    }

    @GetMapping("/sites")
    public List<ConstructionSite> getAllSites() {
        return siteService.getAllSites();
    }

    @GetMapping("/sites/{id}")
    public ConstructionSite getSite(@PathVariable Long id) {
        return siteService.getSite(id);
    }

    @PostMapping("/upload")
    public List<WaterSource> uploadSensorData(@RequestBody SensorUploadRequest request) {
        return siteService.uploadSensorData(request);
    }

    @GetMapping("/sites/{id}/water-sources")
    public List<WaterSource> getWaterSources(@PathVariable Long id) {
        return siteService.getWaterSources(id);
    }

    /**
     * Automatically generates water source predictions using real SoilGrids data
     * for the site's coordinates. No manual upload needed - call this right after
     * creating a site to get predictions.
     */
    @PostMapping("/sites/{id}/auto-predict")
    public List<WaterSource> autoPredict(@PathVariable Long id) {
        return siteService.autoPredict(id);
    }
}
