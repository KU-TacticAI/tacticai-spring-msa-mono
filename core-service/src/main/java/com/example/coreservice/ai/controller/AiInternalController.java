package com.example.coreservice.ai.controller;

import com.example.commonmodule.dto.AiUrlsResponseDto;
import com.example.coreservice.ai.service.AiInternalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/ai")
@RequiredArgsConstructor
public class AiInternalController {

  private final AiInternalService aiInternalService;

  @GetMapping("/list")
  public ResponseEntity<List<AiUrlsResponseDto>> getAiUrls(@RequestParam List<Long> ids) {
    return ResponseEntity.ok(aiInternalService.getAisByIds(ids));
  }
}
