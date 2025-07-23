package com.example.coreservice.ai.entity;

import com.example.commonmodule.base_entity.BaseDeletedAtEntity;
import com.example.commonmodule.enums.AiStatus;
import com.example.coreservice.enums.Tier;
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
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AiAgent extends BaseDeletedAtEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private String name;

  private String gameType;

  private String description;

  private Long score;

  @Enumerated(EnumType.STRING)
  private Tier tier;

  @Enumerated(EnumType.STRING)
  private AiStatus status;

  private String aiUrl = null;

  public void updateName(String name){
    this.name = name;
  }

  public void updateGameType(String gameType){
    this.gameType = gameType;
  }

  public void updateDescription(String description){
    this.description = description;
  }

  public void uploadAiUrl(String aiUrl){
    this.aiUrl = aiUrl;
  }

}
