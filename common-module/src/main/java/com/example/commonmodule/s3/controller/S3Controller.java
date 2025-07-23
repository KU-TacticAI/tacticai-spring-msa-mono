package com.example.commonmodule.s3.controller;

import com.example.commonmodule.s3.dto.FileResponseDto;
import com.example.commonmodule.s3.entity.FileDetail;
import com.example.commonmodule.s3.service.S3Service;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class S3Controller {

  private final S3Service s3Service;

  @PostMapping
  public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
    FileDetail fileDetail = s3Service.uploadFile(file);
    return ResponseEntity.ok(fileDetail.getFilePath());
  }

  @PostMapping("/files")
  public ResponseEntity<List<FileDetail>> uploadFiles(
      @RequestPart(value = "files") List<MultipartFile> files
  ){
    CompletableFuture<List<FileDetail>> urls = s3Service.uploadFiles(files);
    return ResponseEntity.ok(urls.join());
  }

  @GetMapping(value = "/download/zip", produces="application/octet-stream")
  public ResponseEntity<byte[]> downloadGameZip(
      @RequestParam String filePath
  ) {
    FileResponseDto fileDTO = s3Service.getObjectByFilePath(filePath);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentLength(fileDTO.getFile().length);

    String encodedName = URLEncoder.encode(fileDTO.getFileName(), StandardCharsets.UTF_8)
        .replace("+", "%20");
    String contentDisposition = "attachment; filename=\"" + encodedName + "\"";
    headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

    return ResponseEntity.ok().headers(headers).body(fileDTO.getFile());
  }
}
