package com.example.coreservice.ai.service;

import com.example.coreservice.ai.dto.AiResponseDto;
import com.example.coreservice.ai.dto.CreateAiRequestDto;
import com.example.coreservice.ai.dto.DeleteAiRequestDto;
import com.example.coreservice.ai.dto.UpdateAiRequestDto;
import com.example.coreservice.ai.entity.AiAgent;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AiService {

  AiResponseDto createAI(Long userId, CreateAiRequestDto requestDto, MultipartFile file);

  AiResponseDto getAIAgentById(Long id, Long userId);

  List<AiResponseDto> getAllAI(Long userId);

  AiResponseDto updateAi(Long id, Long userId, UpdateAiRequestDto requestDto, MultipartFile file);

  String deleteAIAgent(Long id, Long userId, DeleteAiRequestDto requestDto);

  List<AiAgent> findByUserIdIn(List<Long> userIdList);

  AiResponseDto uploadAiFile(Long aiId, MultipartFile file);
}
