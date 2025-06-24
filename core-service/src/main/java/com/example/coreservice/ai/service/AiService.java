package com.example.coreservice.ai.service;

import com.example.coreservice.ai.dto.AiResponseDto;
import com.example.coreservice.ai.dto.CreateAiRequestDto;
import com.example.coreservice.ai.dto.DeleteAiRequestDto;
import com.example.coreservice.ai.dto.UpdateAiRequestDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AiService {

  AiResponseDto createAI(Long userId, CreateAiRequestDto requestDto);

  AiResponseDto getAIAgentById(Long id, Long userId);

  List<AiResponseDto> getAllAI(Long userId);

  AiResponseDto updateAi(Long id, Long userId, UpdateAiRequestDto requestDto);

  String deleteAIAgent(Long id, Long userId, DeleteAiRequestDto requestDto);
}
