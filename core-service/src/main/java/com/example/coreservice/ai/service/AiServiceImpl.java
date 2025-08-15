package com.example.coreservice.ai.service;

import com.example.commonmodule.s3.entity.FileDetail;
import com.example.commonmodule.s3.service.S3Service;
import com.example.coreservice.ai.dto.AiResponseDto;
import com.example.coreservice.ai.dto.CreateAiRequestDto;
import com.example.coreservice.ai.dto.DeleteAiRequestDto;
import com.example.coreservice.ai.dto.UpdateAiRequestDto;
import com.example.coreservice.ai.entity.AiAgent;
import com.example.coreservice.ai.entity.AiFile;
//import com.example.coreservice.ai.entity.UserAi;
import com.example.coreservice.ai.repository.AiFileRepository;
import com.example.coreservice.ai.repository.AiRepository;
//import com.example.coreservice.ai.repository.UserAiRepository;
import com.example.coreservice.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiServiceImpl implements AiService {

  private final AiRepository aiRepository;
  private final UserService userService;
//  private final UserAiRepository userAiRepository;
  private final S3Service s3Service;
  private final AiFileRepository aiFileRepository;

  @Override
  public AiResponseDto createAI(Long userId, CreateAiRequestDto requestDto) {
    AiAgent aiAgent = AiAgent.builder()
        .userId(userId)
        .name(requestDto.getName())
        .gameType(requestDto.getGameType())
        .description(requestDto.getDescription())
        .build();
    AiAgent savedAi = aiRepository.save(aiAgent);

//    UserAi userAi = UserAi.builder()
//        .userId(userId)
//        .aiId(savedAi.getId())
//        .build();
//    userAiRepository.save(userAi);

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

  @Override
  public List<AiAgent> findByUserIdIn(List<Long> userIdList) {
    return aiRepository.findByUserIdIn(userIdList);
  }

  @Override
  public AiResponseDto uploadAiFile(Long aiId, MultipartFile file) {
    FileDetail uploadAiFile = s3Service.uploadFile(file);
    AiAgent aiAgent = aiRepository.findByIdOrElseThrow(aiId);
    AiFile aiFile = AiFile.builder()
        .aiId(aiId.toString())
        .fileDetailId(uploadAiFile.getId().toString())
        .build();
    aiFileRepository.save(aiFile);
    aiAgent.uploadAiUrl(uploadAiFile.getFilePath());
    AiAgent saveAiAgent = aiRepository.save(aiAgent);
    return AiResponseDto.toDto(saveAiAgent);
  }
}
