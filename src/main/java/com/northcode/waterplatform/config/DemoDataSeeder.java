package com.northcode.waterplatform.config;

import com.northcode.waterplatform.dto.CreateSiteRequest;
import com.northcode.waterplatform.dto.SensorUploadRequest;
import com.northcode.waterplatform.model.ConstructionSite;
import com.northcode.waterplatform.service.SiteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final SiteService siteService;

    public DemoDataSeeder(SiteService siteService) {
        this.siteService = siteService;
    }

    @Override
    public void run(String... args) {
        CreateSiteRequest request = new CreateSiteRequest();
        request.setName("Nairobi Ring Road Extension");
        request.setCountry("Kenya");
        request.setLatitude(-1.286389);
        request.setLongitude(36.817223);

        ConstructionSite site = siteService.createSite(request);

        SensorUploadRequest sensorData = new SensorUploadRequest();
        sensorData.setSiteId(site.getId());
        sensorData.setNdvi(0.42);
        sensorData.setRainfallMm(950);
        sensorData.setTerrainSlopeDegrees(8.5);

        siteService.uploadSensorData(sensorData);

        System.out.println("Demo data ready. Site id=" + site.getId());
        System.out.println("Try: GET  http://localhost:8080/api/sites/" + site.getId() + "/water-sources");
        System.out.println("Try: POST http://localhost:8080/api/sites/" + site.getId() + "/auto-predict (uses real SoilGrids data)");
    }
}
