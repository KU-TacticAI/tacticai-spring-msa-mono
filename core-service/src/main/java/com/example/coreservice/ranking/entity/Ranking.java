package com.example.coreservice.ranking.entity;

import com.example.commonmodule.base_entity.BaseEntity;
import jakarta.persistence.Entity;
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

//  private String username;

  private Long totalScore;

  private Long rankOrder;

//  @Convert(converter = LongListConverter.class)
//  @Column(columnDefinition = "TEXT")
//  private List<Long> aiList;

  public void setRank(Long rank) {
    this.rankOrder = rank;
  }

}
