package com.example.coreservice.user.repository;

import com.example.commonmodule.exceptions.InvalidInputException;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.coreservice.exceptions.UserException;
import com.example.coreservice.user.entity.Users;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

  default Users findByIdOrElseThrow(Long userId){
    Users user = findById(userId).orElseThrow(() -> new NotFoundException(UserException.NOT_FOUND_USER));
    if (user.getDeletedAt() != null){
      throw new InvalidInputException(UserException.DELETED_USER);
    }
    return user;
  }

  Optional<Users> findByEmail(String email);
  default Users findByEmailOrElseThrow(String email) {
    Users user = findByEmail(email).orElseThrow(() -> new NotFoundException(UserException.NOT_FOUND_USER));
    if(user.getDeletedAt() != null) {
      throw new NotFoundException(UserException.DELETED_USER);
    }
    return user;
  }

  boolean existsByEmail(String email);

  List<Users> findByIdIn(List<Long> userIds);
}
