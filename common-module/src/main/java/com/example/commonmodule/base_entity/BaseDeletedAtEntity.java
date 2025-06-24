package com.example.commonmodule.base_entity;

import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseDeletedAtEntity extends BaseEntity {

  private LocalDateTime deletedAt = null;

  public void delete(){
    this.deletedAt = LocalDateTime.now();
  }

}
