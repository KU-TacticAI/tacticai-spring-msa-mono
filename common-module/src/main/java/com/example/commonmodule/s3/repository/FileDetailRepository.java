package com.example.commonmodule.s3.repository;

import com.example.commonmodule.exceptions.FileErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.commonmodule.s3.entity.FileDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDetailRepository extends JpaRepository<FileDetail, Long> {

  Optional<FileDetail> findByOriginFileName(String fileName);
  default FileDetail findByFileNameOrElseThrow(String fileName){
    return findByOriginFileName(fileName).orElseThrow(() -> new NotFoundException(
        FileErrorCode.NOT_FOUND_FILE));
  }

  default FileDetail findByIdOrElseThrow(Long id){
    return findById(id).orElseThrow(() -> new NotFoundException(FileErrorCode.NOT_FOUND_FILE));
  }

  Optional<FileDetail> findByServerFileName(String fileName);
  default FileDetail findByServerFileNameOrElseThrow(String fileName){
    return findByServerFileName(fileName).orElseThrow(() -> new NotFoundException(FileErrorCode.NOT_FOUND_FILE));
  }

  Optional<FileDetail> findByFilePath(String filePath);
  default FileDetail findByFilePathOrElseThrow(String filePath){
    return findByFilePath(filePath).orElseThrow(() -> new NotFoundException(FileErrorCode.NOT_FOUND_FILE));
  }
}
