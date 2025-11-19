package com.example.coreservice.ai.service;

import com.example.commonmodule.enums.AiStatus;
import com.example.commonmodule.enums.Tier;
import com.example.commonmodule.s3.entity.FileDetail;
import com.example.commonmodule.s3.service.S3Service;
import com.example.coreservice.ai.dto.AiResponseDto;
import com.example.coreservice.ai.dto.CreateAiRequestDto;
import com.example.coreservice.ai.dto.DeleteAiRequestDto;
import com.example.coreservice.ai.dto.UpdateAiRequestDto;
import com.example.coreservice.ai.entity.AiAgent;
import com.example.coreservice.ai.entity.AiFile;
import com.example.coreservice.ai.repository.AiFileRepository;
import com.example.coreservice.ai.repository.AiRepository;
import com.example.coreservice.ai_statistics.service.AiStatisticsService;
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
  private final AiStatisticsService aiStatisticsService;

  @Override
  @Transactional
  public AiResponseDto createAI(Long userId, CreateAiRequestDto requestDto, MultipartFile file) {

    FileDetail uploadAiFile = s3Service.uploadFile(file);

    AiAgent aiAgent = AiAgent.builder()
        .userId(userId)
        .name(requestDto.getName())
        .gameType(requestDto.getGameType())
        .description(requestDto.getDescription())
        .version(requestDto.getVersion())
        .aiUrl(uploadAiFile.getFilePath())
        .aiSize(uploadAiFile.getFileSize())
        .score(0L)
        .tier(Tier.BRONZE)
        .status(AiStatus.READY)
        .build();
    AiAgent savedAi = aiRepository.save(aiAgent);

    AiFile aiFile = AiFile.builder()
        .aiId(savedAi.getId().toString())
        .fileDetailId(uploadAiFile.getId().toString())
        .build();
    aiFileRepository.save(aiFile);

//    UserAi userAi = UserAi.builder()
//        .userId(userId)
//        .aiId(savedAi.getId())
//        .build();
//    userAiRepository.save(userAi);

    aiStatisticsService.createAiStatics(savedAi.getId());

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
  public AiResponseDto updateAi(Long id, Long userId, UpdateAiRequestDto requestDto, MultipartFile file) {
    AiAgent aiAgent = aiRepository.findByIdOrElseThrow(id);

    if(requestDto.getName() != null) {
      aiAgent.updateName(requestDto.getName());
    }

    if(requestDto.getVersion() != null) {
      aiAgent.updateVersion(requestDto.getVersion());
    }

    if(requestDto.getDescription() != null) {
      aiAgent.updateDescription(requestDto.getDescription());
    }

    if(file !=null && !file.isEmpty()){
      aiFileRepository.deleteByAiId(aiAgent.getId().toString());

      s3Service.deleteFile(aiAgent.getAiUrl());
      FileDetail updateFile = s3Service.uploadFile(file);
      aiAgent.updateAiUrl(updateFile.getFilePath());
      aiAgent.updateAiSize(updateFile.getFileSize());

      AiFile aiFile = AiFile.builder()
          .aiId(aiAgent.getId().toString())
          .fileDetailId(aiAgent.getId().toString())
          .build();
      aiFileRepository.save(aiFile);
    }

    aiRepository.save(aiAgent);
    return AiResponseDto.toDto(aiAgent);
  }

  @Override
  @Transactional
  public String deleteAIAgent(Long id, Long userId, DeleteAiRequestDto requestDto) {
    userService.checkUserPassword(userId, requestDto.getPassword());

    AiAgent aiAgent = aiRepository.findByIdOrElseThrow(id);

    aiFileRepository.deleteByAiId(aiAgent.getId().toString());

    s3Service.deleteFile(aiAgent.getAiUrl());

    aiAgent.delete();
    aiStatisticsService.delete(aiAgent.getId());
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
