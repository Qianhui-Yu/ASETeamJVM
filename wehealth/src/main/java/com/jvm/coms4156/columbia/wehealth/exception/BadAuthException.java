package com.jvm.coms4156.columbia.wehealth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Authentication failed")
public class BadAuthException extends RuntimeException{
}
