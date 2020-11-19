package com.jvm.coms4156.columbia.wehealth.exception;

import lombok.Data;

@Data
public class ExceptionResponse {

  //private int code;
  private String message;

  public ExceptionResponse(String message) {
    //this.code = code;
    this.message = message;
  }

}
