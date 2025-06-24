package com.example.coreservice.exceptions;

import com.example.commonmodule.exceptions.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AiException  implements ExceptionType {

  DELETED_AI(HttpStatus.BAD_REQUEST, "이미 삭제된 AI입니다."),
  NOT_FOUND_AI(HttpStatus.NOT_FOUND, "AI 를 찾을 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
