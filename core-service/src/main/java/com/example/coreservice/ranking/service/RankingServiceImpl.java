package com.example.coreservice.ranking.service;

import com.example.commonmodule.enums.Tier;
import com.example.coreservice.ai.entity.AiAgent;
import com.example.coreservice.ai.service.AiService;
import com.example.coreservice.ai_statistics.entity.AiStatistics;
import com.example.coreservice.ai_statistics.repository.AiStatisticsRepository;
import com.example.coreservice.ai_statistics.service.AiStatisticsService;
import com.example.coreservice.ai_statistics.service.AiStatisticsServiceImpl;
import com.example.coreservice.ranking.dto.RankingAiResponseDto;
import com.example.coreservice.ranking.dto.RankingResponseDto;
import com.example.coreservice.ranking.entity.Ranking;
import com.example.coreservice.ranking.repository.RankingRepository;
import com.example.coreservice.user.entity.Users;
import com.example.coreservice.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

  private final UserService userService;
  private final AiService aiService;
  private final AiStatisticsService aiStatisticsService;
  private final RankingRepository rankingRepository;

  @Override
  public List<RankingResponseDto> getRankings(Pageable pageable) {
    // 1. 랭킹 페이징 조회
    List<Ranking> rankingList = rankingRepository.findAll(pageable).stream().toList();

    List<Long> userIdList = rankingList.stream()
        .map(Ranking::getUserId)
        .collect(Collectors.toList());

    // 2. 사용자 정보 조회
    Map<Long, Users> userMap = userService.findUsers(userIdList).stream()
        .collect(Collectors.toMap(Users::getId, Function.identity()));

    // 3. AI 정보 조회 (userId별 그룹핑)
    List<AiAgent> aiList = aiService.findByUserIdIn(userIdList);
    Map<Long, List<AiAgent>> userAiMap = aiList.stream()
        .collect(Collectors.groupingBy(AiAgent::getUserId));

    // ▼ [추가] 4. AI 통계 정보 조회 (승률 가져오기 위함)
    List<Long> aiIds = aiList.stream().map(AiAgent::getId).toList();
    List<AiStatistics> statisticsList = aiStatisticsService.findByAiIdIn(aiIds);

    // AI ID를 키로 하여 통계 맵 생성
    Map<Long, AiStatistics> statsMap = statisticsList.stream()
        .collect(Collectors.toMap(AiStatistics::getAiId, Function.identity()));

    // 5. DTO 변환
    return rankingList.stream()
        .map(ranking -> {
          Users user = userMap.get(ranking.getUserId());
          List<AiAgent> userAis = userAiMap.getOrDefault(ranking.getUserId(), Collections.emptyList());

          // AI 리스트를 DTO로 변환 (승률 포함)
          List<RankingAiResponseDto> aiDtoList = userAis.stream()
              .map(ai -> {
                // 해당 AI의 통계 가져오기
                AiStatistics stat = statsMap.get(ai.getId());
                double winRate = (stat != null) ? stat.getWinRate() * 100.0 : 0.0;

                return RankingAiResponseDto.builder()
                    .aiId(ai.getId())
                    .aiName(ai.getName())
                    .gameType(ai.getGameType())
                    .winRate(winRate) // ▼ 승률 설정
                    .tier(ai.getTier()) // 티어는 AiAgent에 있다고 가정 (없으면 stat.getTier())
                    .build();
              })
              .collect(Collectors.toList());

          return RankingResponseDto.toDto(ranking, user, aiDtoList);
        })
        .collect(Collectors.toList());
  }

  @Transactional
  public void recalculateAllUserRanking() {
    // 1. 기존 랭킹 데이터 초기화
    rankingRepository.deleteAll();

    // 2. 모든 유저 조회
    List<Users> allUsers = userService.findAllUsers();
    List<Ranking> newRankings = new ArrayList<>();

    // ▼ [추가] 변경된 AI들을 한 번에 저장하기 위한 리스트
    List<AiAgent> updatedAiList = new ArrayList<>();

    for (Users user : allUsers) {
      // 3. 유저의 AI 및 통계 조회
      List<AiAgent> userAis = aiService.getAiAgentsByUserId(user.getId());
      List<Long> aiIds = userAis.stream().map(AiAgent::getId).toList();
      List<AiStatistics> statsList = aiStatisticsService.findByAiIdIn(aiIds);

      // 통계 리스트를 Map으로 변환 (AI ID로 쉽게 찾기 위해)
      Map<Long, AiStatistics> statsMap = statsList.stream()
          .collect(Collectors.toMap(AiStatistics::getAiId, Function.identity()));

      long totalWins = 0;
      long totalLosses = 0;
      long totalDraws = 0;
      long totalGames = 0;

      // 4. 개별 AI의 승률 계산 및 티어 갱신 로직 추가
      for (AiAgent ai : userAis) {
        AiStatistics stat = statsMap.get(ai.getId());

        if (stat != null) {
          // 유저 통합 통계 합산
          totalWins += stat.getWins();
          totalLosses += stat.getLosses();
          totalDraws += stat.getDraws();
          totalGames += stat.getGameCount();

          // ▼ [핵심] 개별 AI 승률 계산
          double aiWinRate = 0.0;
          if (stat.getGameCount() > 0) {
            aiWinRate = (double) stat.getWins() / stat.getGameCount() * 100.0;
          }

          // ▼ [핵심] 티어 계산 및 적용
          Tier newTier = calculateTier(aiWinRate);

          // 기존 티어와 다르면 업데이트
          if (ai.getTier() != newTier) {
            ai.updateTier(newTier); // AiAgent에 setTier 메서드 필요 (Lombok @Setter)
            updatedAiList.add(ai);
          }
        }
      }

      // 5. 유저 통합 승률 계산 (기존 로직)
      double userWinRate = 0.0;
      if (totalGames > 0) {
        userWinRate = (double) totalWins / totalGames * 100.0;
      }

      Ranking ranking = Ranking.builder()
          .userId(user.getId())
          .winRate(userWinRate)
          .winCount(totalWins)
          .loseCount(totalLosses)
          .totalGameCount(totalGames)
          .build();

      newRankings.add(ranking);
    }

    // 6. 변경된 AI 정보(티어) 일괄 저장
    if (!updatedAiList.isEmpty()) {
      aiService.saveAll(updatedAiList); // AiService에 saveAll 추가 필요
    }

    // 7. 랭킹 정렬 및 저장 (기존 로직)
    newRankings.sort(
        Comparator.comparing(Ranking::getWinRate).reversed()
            .thenComparing(Comparator.comparing(Ranking::getWinCount).reversed())
    );

    for (int i = 0; i < newRankings.size(); i++) {
      Ranking current = newRankings.get(i);
      if (i == 0) {
        current.setRank(1L);
      } else {
        Ranking prev = newRankings.get(i - 1);
        if (Double.compare(current.getWinRate(), prev.getWinRate()) == 0) {
          current.setRank(prev.getRankOrder());
        } else {
          current.setRank((long) (i + 1));
        }
      }
    }
    rankingRepository.saveAll(newRankings);

    // 8. 유저 테이블 랭킹 업데이트 (기존 로직 유지)
    for (Ranking ranking : newRankings) {
      Users user = allUsers.stream()
          .filter(u -> u.getId().equals(ranking.getUserId()))
          .findFirst().orElse(null);
      if (user != null) user.setRanking(ranking.getRankOrder());
    }
    userService.saveAll(allUsers);
  }

  // ▼ 티어 계산 헬퍼 메서드
  private Tier calculateTier(double winRate) {
    if (winRate >= 90.0) return Tier.PLATINUM;
    if (winRate >= 80.0) return Tier.GOLD;
    if (winRate >= 50.0) return Tier.SILVER;
    return Tier.BRONZE;
  }
}
