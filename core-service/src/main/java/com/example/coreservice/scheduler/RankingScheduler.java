package com.example.coreservice.scheduler;

import com.example.coreservice.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingScheduler {

  private final RankingService rankingService;

  /**
   * 매일 오전 12시에 실행
   */
  @Scheduled(cron = "0 0 0 * * *") // 초 분 시 일 월 요일
//  @Scheduled(cron = "0/10 * * * * *")
    public void updateRankingBatch() {
    log.info("🏁 랭킹 재계산 시작");
    rankingService.recalculateAllUserRanking();
    log.info("✅ 랭킹 재계산 완료");
  }
}
