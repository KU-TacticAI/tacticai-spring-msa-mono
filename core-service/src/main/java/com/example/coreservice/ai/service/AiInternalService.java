package com.example.coreservice.ai.service;

import com.example.commonmodule.dto.AiUrlsResponseDto;
import com.example.coreservice.ai.entity.AiAgent;
import com.example.coreservice.ai.repository.AiRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiInternalService {

  private final AiRepository aiRepository;

  public List<AiUrlsResponseDto> getAisByIds(List<Long> ids) {
    List<AiAgent> aiAgentList = aiRepository.findByIdIn(ids);
    return aiAgentList.stream().map(aiAgent -> {
      return new AiUrlsResponseDto(aiAgent.getId(), aiAgent.getAiUrl());
    }).toList();
  }
}
