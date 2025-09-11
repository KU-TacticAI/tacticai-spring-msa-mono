package com.example.coreservice.ai.repository;

import com.example.coreservice.ai.entity.AiFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiFileRepository extends JpaRepository<AiFile, Long> {

  void deleteByAiId(String aiId);
}
