package com.example.coreservice.ai.controller;

import com.example.commonmodule.util.JWTUtil;
import com.example.coreservice.ai.dto.AiResponseDto;
import com.example.coreservice.ai.dto.CreateAiRequestDto;
import com.example.coreservice.ai.dto.DeleteAiRequestDto;
import com.example.coreservice.ai.dto.UpdateAiRequestDto;
import com.example.coreservice.ai.service.AiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

  private final AiService aiService;
  private final JWTUtil jwtUtil;

  @PostMapping
  public ResponseEntity<AiResponseDto> createAIAgent(
      @RequestHeader("Authorization") String token,
      @RequestPart CreateAiRequestDto requestDto,
      @RequestPart MultipartFile file
  ) {
    Long userId = Long.parseLong(jwtUtil.getUserId(token));
    return ResponseEntity.ok().body(aiService.createAI(userId, requestDto, file));
  }

  @PostMapping("/{aiId}/files")
  public ResponseEntity<AiResponseDto> createAIFile(
      @PathVariable Long aiId,
      @RequestPart MultipartFile file
  ){
    return ResponseEntity.ok().body(aiService.uploadAiFile(aiId, file));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AiResponseDto> getAIAgent(
      @PathVariable Long id,
      @RequestHeader("Authorization") String token
      ) {
    Long userId = Long.parseLong(jwtUtil.getUserId(token));
    return ResponseEntity.ok().body(aiService.getAIAgentById(id, userId));
  }

  @GetMapping
  public ResponseEntity<List<AiResponseDto>> getAllAIAgents(
      @RequestHeader("Authorization") String token
  ) {
    Long userId = Long.parseLong(jwtUtil.getUserId(token));
    return ResponseEntity.ok().body(aiService.getAllAI(userId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AiResponseDto> updateAIAgent(
      @PathVariable Long id,
      @RequestHeader("Authorization") String token,
      @RequestPart(required = false) UpdateAiRequestDto requestDto,
      @RequestPart(required = false) MultipartFile file
  ) {
    Long userId = Long.parseLong(jwtUtil.getUserId(token));
    return ResponseEntity.ok().body(aiService.updateAi(id, userId, requestDto, file));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteAIAgent(
      @PathVariable Long id,
      @RequestHeader("Authorization") String token,
      @RequestBody DeleteAiRequestDto requestDto) {
    Long userId = Long.parseLong(jwtUtil.getUserId(token));
    return ResponseEntity.ok().body(aiService.deleteAIAgent(id, userId, requestDto));
  }

}
