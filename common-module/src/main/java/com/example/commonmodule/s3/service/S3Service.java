package com.example.commonmodule.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.commonmodule.exceptions.FileErrorCode;
import com.example.commonmodule.exceptions.InternalServerException;
import com.example.commonmodule.s3.dto.FileResponseDto;
import com.example.commonmodule.s3.entity.FileDetail;
import com.example.commonmodule.s3.repository.FileDetailRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class S3Service {

  @Value("${cloud.aws.s3.bucket}")
  private String imageBucket;

  private final FileDetailRepository fileDetailRepository;
  private final AmazonS3 s3;

  /**
   * S3에 파일 업로드
   */
  @Transactional
  public FileDetail uploadFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    String bucket = determineBucket(file);
    String fileName = generateUniqueFileName(file.getOriginalFilename());
    String filePath = uploadToS3(bucket, file, fileName);

    // 파일 정보 저장
    FileDetail fileDetail = new FileDetail(
        bucket,
        file.getOriginalFilename(),
        fileName,
        filePath,
        file.getSize(), file.getContentType()
    );
    return fileDetailRepository.save(fileDetail);
  }

  /**
   * 다중 파일 업로드 (비동기 처리)
   */
  @Async
  @Transactional
  public CompletableFuture<List<FileDetail>> uploadFiles(List<MultipartFile> files) {
    List<FileDetail> uploadedFiles = files.stream()
        .map(this::uploadFile)
        .collect(Collectors.toList());
    return CompletableFuture.completedFuture(uploadedFiles);
  }

  /**
   * S3에서 파일 삭제 (트랜잭션 분리)
   */
  public void deleteFile(String filePath) {
    FileDetail fileDetail = fileDetailRepository.findByFilePathOrElseThrow(filePath);

    // S3 삭제
    try {
      s3.deleteObject(new DeleteObjectRequest(fileDetail.getBucket(), fileDetail.getServerFileName()));
    } catch (Exception e) {
      throw new InternalServerException(FileErrorCode.FAIL_UPLOAD_FILE);
    }

    // DB 삭제 (트랜잭션 적용)
    deleteFileFromDB(fileDetail);
  }

  /**
   * DB에서 파일 정보 삭제 (트랜잭션 적용)
   */
  @Transactional
  public void deleteFileFromDB(FileDetail fileDetail) {
    fileDetailRepository.delete(fileDetail);
  }

  /**
   * S3에서 파일 다운로드
   */
  public FileResponseDto getObjectByFilePath(String filePath) {
    FileDetail fileDetail = fileDetailRepository.findByFilePathOrElseThrow(filePath);
    String bucket = fileDetail.getBucket();

    try (S3ObjectInputStream inputStream = getS3FileStream(bucket, fileDetail.getServerFileName())) {
      return new FileResponseDto(IOUtils.toByteArray(inputStream), fileDetail.getOriginFileName());
    } catch (IOException e) {
      throw new InternalServerException(FileErrorCode.NOT_FOUND_FILE);
    }
  }

  /**
   * 파일의 버킷 결정 (이미지 vs 게임 파일)
   */
  private String determineBucket(MultipartFile file) {
    String fileType = getFileExtension(file.getOriginalFilename());
//    return ".zip".equals(fileType) ? gameBucket : imageBucket;
    return imageBucket;
  }

  /**
   * S3 업로드 실행
   */
  private String uploadToS3(String bucket, MultipartFile file, String fileName) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());

    try (InputStream inputStream = file.getInputStream()) {
      s3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
    } catch (IOException e) {
      throw new InternalServerException(FileErrorCode.FAIL_UPLOAD_FILE);
    }

    return "https://" + "s3.ap-northeast-2.amazonaws.com/" + bucket + "/" + fileName;
  }

  /**
   * S3 파일 스트림 가져오기
   */
  private S3ObjectInputStream getS3FileStream(String bucket, String fileName) {
    S3Object s3Object = s3.getObject(new GetObjectRequest(bucket, fileName));
    return s3Object.getObjectContent();
  }

  /**
   * 난수화된 파일명 생성
   */
  private String generateUniqueFileName(String fileName) {
    return LocalDateTime.now() + "_" + UUID.randomUUID() + getFileExtension(fileName);
  }

  /**
   * 파일 확장자 추출
   */
  private String getFileExtension(String fileName) {
    try {
      return fileName.substring(fileName.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일: " + fileName);
    }
  }
}