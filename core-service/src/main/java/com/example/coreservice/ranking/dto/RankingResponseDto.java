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
  private Long rank;
  private Double winRate;
  private String record;

  // ▼ [추가] AI 목록 필드 추가
  private List<RankingAiResponseDto> aiList;

  // ▼ [수정] aiList를 인자로 받도록 변경
  public static RankingResponseDto toDto(Ranking ranking, Users user, List<RankingAiResponseDto> aiList) {
    return RankingResponseDto.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .rank(ranking.getRankOrder())
        .winRate(ranking.getWinRate())
        .record(ranking.getWinCount() + "승 " + ranking.getLoseCount() + "패")
        .aiList(aiList) // 여기에 담아줍니다
        .build();
  }
}