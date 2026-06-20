package com.northcode.waterplatform.controller;

import com.northcode.waterplatform.model.ConstructionSite;
import com.northcode.waterplatform.model.WaterQuality;
import com.northcode.waterplatform.service.SiteService;
import com.northcode.waterplatform.service.WaterQualityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // open for the hackathon demo - works for React Native dev too
public class WaterQualityController {

    private final WaterQualityService waterQualityService;
    private final SiteService siteService;

    public WaterQualityController(WaterQualityService waterQualityService, SiteService siteService) {
        this.waterQualityService = waterQualityService;
        this.siteService = siteService;
    }

    /**
     * Assesses water quality for a construction site (pH + turbidity proxy from air quality).
     * Call this after creating a site, alongside /auto-predict for water source locations.
     */
    @PostMapping("/sites/{id}/water-quality")
    public WaterQuality assessQuality(@PathVariable Long id) {
        ConstructionSite site = siteService.getSite(id);
        return waterQualityService.assessWaterQuality(site);
    }

    @GetMapping("/sites/{id}/water-quality")
    public List<WaterQuality> getQualityHistory(@PathVariable Long id) {
        return waterQualityService.getQualityHistory(id);
    }
}
