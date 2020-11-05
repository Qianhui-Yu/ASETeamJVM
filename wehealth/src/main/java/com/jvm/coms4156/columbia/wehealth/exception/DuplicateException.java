package com.jvm.coms4156.columbia.wehealth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Ethan on 11/04/2020.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DuplicateException extends Exception {
  public DuplicateException(String message) {
    super(message);
  }
}
