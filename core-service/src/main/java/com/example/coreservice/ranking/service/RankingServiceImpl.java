package com.example.coreservice.ranking.service;

import com.example.coreservice.ai.entity.AiAgent;
import com.example.coreservice.ai.service.AiService;
import com.example.coreservice.ranking.dto.RankingResponseDto;
import com.example.coreservice.ranking.entity.Ranking;
import com.example.coreservice.ranking.repository.RankingRepository;
import com.example.coreservice.user.entity.Users;
import com.example.coreservice.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Collections;
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
  private final RankingRepository rankingRepository;
//  private final UserAiRepository userAiRepository;

  @Override
  public List<RankingResponseDto> getRankings(Long totalScore, Pageable pageable) {
    List<Ranking> rankingList = rankingRepository.findAll(pageable).stream().toList();

    List<Long> userIdList = rankingList.stream()
        .map(Ranking::getUserId)
        .collect(Collectors.toList());

    // 사용자 정보 조회
    Map<Long, Users> userMap = userService.findUsers(userIdList).stream()
        .collect(Collectors.toMap(Users::getId, Function.identity()));

    // AI 정보 조회 (userId별로 그룹핑)
    List<AiAgent> aiList = aiService.findByUserIdIn(userIdList);
    Map<Long, List<AiAgent>> userAiMap = aiList.stream()
        .collect(Collectors.groupingBy(AiAgent::getUserId));

    // DTO 변환
    return rankingList.stream()
        .map(ranking -> {
          Users user = userMap.get(ranking.getUserId());
          List<AiAgent> userAis = userAiMap.getOrDefault(ranking.getUserId(), Collections.emptyList());

          return RankingResponseDto.toDto(ranking, user, userAis);
        })
        .collect(Collectors.toList());
  }

  @Transactional
  public void recalculateAllUserRanking() {
    List<Ranking> allRankings = rankingRepository.findAll(Sort.by(Sort.Direction.DESC, "totalScore"));
    List<Users> allUsers = userService.findUsers(allRankings.stream().map(Ranking::getUserId).collect(Collectors.toList()));
    for (int i = 0; i < allRankings.size(); i++) {
      Ranking ranking = allRankings.get(i);
      ranking.setRank((long) (i + 1));
      Users user = allUsers.get(i);
      user.setRanking((long) (i + 1));
      rankingRepository.save(ranking);
    }
  }
}
