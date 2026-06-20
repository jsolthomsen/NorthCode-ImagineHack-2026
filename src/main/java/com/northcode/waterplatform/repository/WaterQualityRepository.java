package com.northcode.waterplatform.repository;

import com.northcode.waterplatform.model.WaterQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WaterQualityRepository extends JpaRepository<WaterQuality, Long> {
    List<WaterQuality> findBySiteId(Long siteId);
}
