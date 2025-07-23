package com.example.coreservice.ranking.dto;

import com.example.coreservice.ai.entity.AiAgent;
import com.example.coreservice.ranking.entity.Ranking;
import com.example.coreservice.user.entity.Users;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponseDto {

  private Long userId;

  private String username;

  private Long totalScore;

  private Long rank;

  private List<RankingAiResponseDto> aiList;

  public static RankingResponseDto toDto(Ranking ranking, Users user, List<AiAgent> aiAgents) {
    List<RankingAiResponseDto> aiList = aiAgents.stream()
        .map(ai -> RankingAiResponseDto.builder()
            .aiId(ai.getId())
            .aiName(ai.getName())
            .gameType(ai.getGameType())
            .score(ai.getScore())
            .tier(ai.getTier())
            .build())
        .collect(Collectors.toList());

    return RankingResponseDto.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .totalScore(ranking.getTotalScore())
        .rank(ranking.getRankOrder())
        .aiList(aiList)
        .build();
  }

}
