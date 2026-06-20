package com.northcode.waterplatform.repository;

import com.northcode.waterplatform.model.WaterSource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WaterSourceRepository extends JpaRepository<WaterSource, Long> {
    List<WaterSource> findBySiteIdOrderByProbabilityScoreDesc(Long siteId);
}
