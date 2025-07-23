package com.example.coreservice.ai.repository;

import com.example.coreservice.ai.entity.UserAi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAiRepository extends JpaRepository<UserAi, Long> {

}
