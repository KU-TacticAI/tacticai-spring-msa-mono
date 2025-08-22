package com.example.coreservice.user.entity;

import com.example.commonmodule.base_entity.BaseDeletedAtEntity;
import com.example.coreservice.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  private String profileLink;

  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  private Long ranking;

  public void updateNickname(String newNickname) {
    this.nickname = newNickname;
  }

  public void updatePassword(String newPassword) {
    this.password = newPassword;
  }

  public void updateProfileLink(String profileLink) {
    this.profileLink = profileLink;
  }

  public void setRanking(Long ranking) {
    this.ranking = ranking;
  }
}
