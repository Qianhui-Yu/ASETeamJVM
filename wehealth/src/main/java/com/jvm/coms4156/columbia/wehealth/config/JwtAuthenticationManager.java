package com.jvm.coms4156.columbia.wehealth.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthenticationManager implements AuthenticationManager {

  @Override
  public Authentication authenticate(Authentication authentication) throws
      AuthenticationException {
    return null;
  }
}
