package com.example.commonmodule.advice;

import com.example.commonmodule.exceptions.DuplicatedException;
import com.example.commonmodule.exceptions.ExceptionResponseDto;
import com.example.commonmodule.exceptions.InternalServerException;
import com.example.commonmodule.exceptions.InvalidInputException;
import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {

  //커스텀
  @ExceptionHandler
  public ResponseEntity<ExceptionResponseDto> duplicatedException(DuplicatedException e) {
    return createErrorResponse(e.getHttpStatus(), e.getErrorName(), e.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<ExceptionResponseDto> internalServerException(InternalServerException e) {
    return createErrorResponse(e.getHttpStatus(), e.getErrorName(), e.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<ExceptionResponseDto> invalidInputException(InvalidInputException e) {
    return createErrorResponse(e.getHttpStatus(), e.getErrorName(), e.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<ExceptionResponseDto> notFoundException(NotFoundException e) {
    return createErrorResponse(e.getHttpStatus(), e.getErrorName(), e.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<ExceptionResponseDto> noAuthorizedException(NoAuthorizedException e) {
    return createErrorResponse(e.getHttpStatus(), e.getErrorName(), e.getMessage());
  }

  private ResponseEntity<ExceptionResponseDto> createErrorResponse(HttpStatus status,
      String errorName, String message) {
    ExceptionResponseDto responseDto = new ExceptionResponseDto(
        status.getReasonPhrase(),
        errorName,
        message
    );
    return new ResponseEntity<>(responseDto, status);
  }
}