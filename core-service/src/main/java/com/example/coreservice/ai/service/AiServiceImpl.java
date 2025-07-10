package com.example.coreservice.ai.service;

import com.example.coreservice.ai.dto.AiResponseDto;
import com.example.coreservice.ai.dto.CreateAiRequestDto;
import com.example.coreservice.ai.dto.DeleteAiRequestDto;
import com.example.coreservice.ai.dto.UpdateAiRequestDto;
import com.example.coreservice.ai.entity.AiAgent;
import com.example.coreservice.ai.repository.AiRepository;
import com.example.coreservice.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiServiceImpl implements AiService {

  private final AiRepository aiRepository;
  private final UserService userService;

  @Override
  public AiResponseDto createAI(Long userId, CreateAiRequestDto requestDto) {
    AiAgent aiAgent = AiAgent.builder()
        .userId(userId)
        .name(requestDto.getName())
        .gameType(requestDto.getGameType())
        .description(requestDto.getDescription())
        .build();
    aiRepository.save(aiAgent);
    return AiResponseDto.toDto(aiAgent);
  }

  @Override
  public AiResponseDto getAIAgentById(Long id, Long userId) {
    AiAgent aiAgent = aiRepository.findByIdOrElseThrow(id);
    return AiResponseDto.toDto(aiAgent);
  }

  @Override
  public List<AiResponseDto> getAllAI(Long userId) {
    return aiRepository.findByUserIdOrElseThrow(userId);
  }

  @Override
  @Transactional
  public AiResponseDto updateAi(Long id, Long userId, UpdateAiRequestDto requestDto) {
    AiAgent aiAgent = aiRepository.findByIdOrElseThrow(id);

    if(requestDto.getName() != null) {
      aiAgent.updateName(requestDto.getName());
    }

    if(requestDto.getGameType() != null) {
      aiAgent.updateGameType(requestDto.getGameType());
    }

    if(requestDto.getDescription() != null) {
      aiAgent.updateDescription(requestDto.getDescription());
    }

    aiRepository.save(aiAgent);
    return AiResponseDto.toDto(aiAgent);
  }

  @Override
  @Transactional
  public String deleteAIAgent(Long id, Long userId, DeleteAiRequestDto requestDto) {
    userService.checkUserPassword(userId, requestDto.getPassword());

    AiAgent aiAgent = aiRepository.findByIdOrElseThrow(id);
    aiAgent.delete();
    aiRepository.save(aiAgent);
    return "삭제되었습니다.";
  }
}
