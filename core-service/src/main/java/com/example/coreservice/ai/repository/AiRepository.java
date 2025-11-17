package com.example.coreservice.ai.repository;

import com.example.commonmodule.exceptions.NotFoundException;
import com.example.coreservice.ai.dto.AiResponseDto;
import com.example.coreservice.ai.entity.AiAgent;
import com.example.coreservice.exceptions.AiException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRepository extends JpaRepository<AiAgent, Long> {

  default AiAgent findByIdOrElseThrow(Long id){
    AiAgent aiAgent = findById(id).orElseThrow(() -> new NotFoundException(AiException.NOT_FOUND_AI));
    if (aiAgent.getDeletedAt() != null){
      throw new NotFoundException(AiException.DELETED_AI);
    }
    return aiAgent;
  }

  List<AiAgent> findByUserIdAndDeletedAtIsNull(Long userId);

  default List<AiResponseDto> findByUserIdOrElseThrow(Long userId){
    List<AiAgent> aiList = findByUserIdAndDeletedAtIsNull(userId);
    return aiList.stream().map(AiResponseDto::toDto).toList();
  }

  List<AiAgent> findByUserIdIn(List<Long> userIdList);

  List<AiAgent> findByIdIn(List<Long> ids);

  List<AiAgent> getAiAgentsByUserId(long l);
}
