package com.example.coreservice.ai_statistics.repository;

import com.example.coreservice.ai_statistics.entity.AiStatistics;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiStatisticsRepository extends JpaRepository<AiStatistics, Long> {

  List<AiStatistics> findByAiIdIn(List<Long> aiIds);
}
