package com.example.coreservice.ai.controller;

import com.example.coreservice.ai.dto.AiResponseDto;
import com.example.coreservice.ai.dto.CreateAiRequestDto;
import com.example.coreservice.ai.dto.DeleteAiRequestDto;
import com.example.coreservice.ai.dto.UpdateAiRequestDto;
import com.example.coreservice.ai.service.AiService;
import com.example.coreservice.ai.service.AiServiceImpl;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

  private final AiService aiService;

  @PostMapping("/user/{userId}")
  public ResponseEntity<AiResponseDto> createAIAgent(
      @PathVariable Long userId,
      @RequestBody CreateAiRequestDto requestDto
  ) {
    return ResponseEntity.ok().body(aiService.createAI(userId, requestDto));
  }

  @GetMapping("/{id}/user/{userId}")
  public ResponseEntity<AiResponseDto> getAIAgent(
      @PathVariable Long id,
      @PathVariable Long userId
      ) {
    return ResponseEntity.ok().body(aiService.getAIAgentById(id, userId));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<AiResponseDto>> getAllAIAgents(
      @PathVariable Long userId
  ) {
    return ResponseEntity.ok().body(aiService.getAllAI(userId));
  }

  @PutMapping("/{id}/user/{userId}")
  public ResponseEntity<AiResponseDto> updateAIAgent(
      @PathVariable Long id,
      @PathVariable Long userId,
      @RequestBody UpdateAiRequestDto requestDto
  ) {
    return ResponseEntity.ok().body(aiService.updateAi(id, userId, requestDto));
  }

  @DeleteMapping("/{id}/user/{userId}")
  public ResponseEntity<String> deleteAIAgent(
      @PathVariable Long id,
      @PathVariable Long userId,
      @RequestBody DeleteAiRequestDto requestDto) {
    return ResponseEntity.ok().body(aiService.deleteAIAgent(id, userId, requestDto));
  }

}
