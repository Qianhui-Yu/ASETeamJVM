package com.jvm.coms4156.columbia.wehealth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Ethan on 11/04/2020.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class MissingDataException extends RuntimeException {

  public MissingDataException(String message) {
    super(message);
  }
}
