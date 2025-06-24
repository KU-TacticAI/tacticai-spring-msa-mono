package com.example.coreservice.user.entity;

import com.example.commonmodule.base_entity.BaseDeletedAtEntity;
import com.example.commonmodule.base_entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Users extends BaseDeletedAtEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, length = 100)
  private String email;

  private String password;

  private String username;

  private String nickname;

  public void updateNickname(String newNickname) {
    this.nickname = newNickname;
  }

  public void updatePassword(String newPassword) {
    this.password = newPassword;
  }
}
