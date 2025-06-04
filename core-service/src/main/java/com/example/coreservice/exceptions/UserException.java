package com.example.coreservice.exceptions;

import com.example.commonmodule.exceptions.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserException implements ExceptionType {

  //InvalidInputException
  //비밀번호가 틀렸을 때 출력하는 오류 메시지
  WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀 번호가 틀렸습니다."),
  WRONG_INPUT(HttpStatus.BAD_REQUEST, "입력값이 잘못되었습니다."),

  // NotFoundException
  NOT_FOUND_USER(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
  DELETED_USER(HttpStatus.BAD_REQUEST, "이미 삭제된 회원입니다."),
  AUTH_NOT_EMAIL(HttpStatus.BAD_REQUEST, "이메일 인증에 실패하였습니다."),

  // NoAuthorizedException
  NOT_AUTHORIZED_MANAGER(HttpStatus.UNAUTHORIZED, "유저만 권한을 변경 할 수 있습니다."),
  EXIST_SOCIAL(HttpStatus.BAD_REQUEST, "다른 소셜아이디가 있습니다."),
  NOT_LOGIN(HttpStatus.BAD_REQUEST, "다시 로그인을 해주시기바랍니다."),

  // DuplicatedException
  //중복된 이메일로 가일 할 때 출력하는 오류 메시지
  EMAIL_EXIST(HttpStatus.BAD_REQUEST, "중복된 아이디 입니다."),
  EXIST_ADMIN(HttpStatus.BAD_REQUEST, "관리자가 이미 존재합니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
