package com.jvm.coms4156.columbia.wehealth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

  //  @ExceptionHandler(BadRequestException.class)
  //  @ResponseStatus(HttpStatus.BAD_REQUEST)
  //  public ExceptionResponse handleBadRequestException(Exception e) {
  //    return new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
  //  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ExceptionResponse> handleBadRequestException(Exception e) {
    return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
  }
}
