package com.example.coreservice.ranking.entity;

import com.example.commonmodule.base_entity.BaseEntity;
import com.example.commonmodule.enums.GameType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ranking extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  // ▼ 기존 totalScore 삭제하고 승률 관련 필드 추가
  private Double winRate; // 유저의 통합 승률 (%)

  private Long totalGameCount; // 유저가 가진 AI들의 총 게임 수

  private Long winCount;  // 유저가 가진 AI들의 총 승리 수

  private Long loseCount; // 유저가 가진 AI들의 총 패배 수

  private Long rankOrder; // 순위

  public void setRank(Long rank) {
    this.rankOrder = rank;
  }

}
