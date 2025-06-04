package com.example.coreservice.user.repository;

import com.example.commonmodule.exceptions.NotFoundException;
import com.example.coreservice.exceptions.UserException;
import com.example.coreservice.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

  default Users findByIdOrElseThrow(Long userId){
    return findById(userId).orElseThrow(() -> new NotFoundException(UserException.NOT_FOUND_USER));
  }

  boolean existsByEmail(String email);
}
