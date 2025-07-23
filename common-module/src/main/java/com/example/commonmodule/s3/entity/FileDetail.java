package com.example.commonmodule.s3.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FileDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String bucket;

  private String filePath;

  private Long fileSize;

  private String fileType;

  private String originFileName;

  private String serverFileName;

  public FileDetail(String bucket, String originFileName, String serverFileName, String filePath, Long fileSize, String fileType) {
    this.bucket = bucket;
    this.originFileName = originFileName;
    this.serverFileName = serverFileName;
    this.filePath = filePath;
    this.fileSize = fileSize;
    this.fileType = fileType;
  }

}
