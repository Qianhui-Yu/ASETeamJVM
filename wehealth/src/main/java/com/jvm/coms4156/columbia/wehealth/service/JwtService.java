package com.jvm.coms4156.columbia.wehealth.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.exception.BadAuthException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * Created by Ethan on 11/04/2020.
 */
@Service
@Slf4j
public class JwtService {
  private final String secret;
  private final long expiration;

  public JwtService(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration}") int expiration) {
    this.secret = secret;
    this.expiration = expiration;
  }

  public long getExpiration() {
    return System.currentTimeMillis() + (expiration * 60L * 1000L);
  }

  public String generate(Long id, int userType, long exp) {
    return JWT.create()
        .withClaim("userId", id)
        .withClaim("userType", userType)
        .withExpiresAt(new Date(exp))
        .sign(Algorithm.HMAC512(secret.getBytes(StandardCharsets.UTF_8)));
  }

  public AuthenticatedUser verify(String token) throws BadAuthException {
    try {
      DecodedJWT jwt = JWT.require(Algorithm.HMAC512(secret.getBytes(StandardCharsets.UTF_8)))
          .build()
          .verify(token);
      return new AuthenticatedUser(jwt.getClaim("userId").asLong(),
              jwt.getClaim("userType").asInt());
    } catch (TokenExpiredException e) {
      log.error("Expired token");
      throw new BadAuthException();
    } catch (Throwable t) {
      log.error("Error while verifying JWT token", t);
      throw new BadAuthException();
    }
  }


}
