package com.northcode.waterplatform.repository;

import com.northcode.waterplatform.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    List<SensorReading> findBySiteId(Long siteId);
}
