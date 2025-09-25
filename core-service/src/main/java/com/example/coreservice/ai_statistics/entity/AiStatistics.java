package com.example.coreservice.ai_statistics.entity;

import com.example.commonmodule.base_entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AiStatistics extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long aiId;

  private int gameCount;

  private int wins;

  private int losses;

  private int draws;

  private double avg_turns;

  private int avgResponseTimeMs;

  private double winRate;

}
