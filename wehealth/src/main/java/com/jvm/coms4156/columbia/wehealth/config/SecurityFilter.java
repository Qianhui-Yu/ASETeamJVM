package com.jvm.coms4156.columbia.wehealth.config;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.exception.BadAuthException;
import com.jvm.coms4156.columbia.wehealth.service.JwtService;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

@Slf4j
public class SecurityFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtService jwtService;

  public SecurityFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public void doFilter(ServletRequest servletRequest,
                       ServletResponse servletResponse,
                       FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) servletRequest;
    HttpServletResponse resp = (HttpServletResponse) servletResponse;

    String remoteAddress = getRemoteAddress(req);

    String token = getToken(req);
    AuthenticatedUser user = null;
    if (token != null) {
      try {
        user = decodeAndRefreshJwt(resp, token);
      } catch (BadAuthException e) {
        log.error("Auth token is invalid");
      }
    }

    try {
      filterChain.doFilter(servletRequest, servletResponse);
      resp.addHeader("X-XSS-Protection", "1");
      log.info(String.format("Request %s %s %s %d", remoteAddress, req.getRequestURI(),
              user == null ? "Anonymous" : user.getUserId(), resp.getStatus()));
    } catch (ServletException | IOException e) {
      log.error(String.format("Request %s %s %s %d %s", remoteAddress, req.getRequestURI(),
              user == null ? "Anonymous" : user.getUserId(), resp.getStatus(), e.toString()), e);
      throw e;
    } catch (Throwable t) {
      log.error(String.format("Request %s %s %s %d %s", remoteAddress, req.getRequestURI(),
              user == null ? "Anonymous" : user.getUserId(), resp.getStatus(), t.toString()), t);
      throw new ServletException(t);
    }
  }

  private AuthenticatedUser decodeAndRefreshJwt(HttpServletResponse resp,
                                                String token)
          throws AuthenticationException, BadAuthException {
    AuthenticatedUser user = jwtService.verify(token);
    //AuthenticatedUser user = new AuthenticatedUser(10L , 0, "Test");
    SecurityContextHolder.getContext().setAuthentication(user);

    Cookie cookie = new Cookie("authToken", jwtService.generate(user.getUsername(),
            user.getUserId(), user.getUserType(), jwtService.getExpiration()));
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    resp.addCookie(cookie);
    return user;
  }

  private String getToken(HttpServletRequest req) {
    String token = null;
    String header = req.getHeader("Authorization");
    if (!StringUtils.isEmpty(header) && header.startsWith("Bearer ")) {
      token = header.replace("Bearer ", "");
    }
    if (token == null && req.getCookies() != null) {
      token = Arrays.stream(req.getCookies()).filter(c -> "authToken".equals(c.getName()))
              .map(Cookie::getValue).findFirst().orElse(null);
    }
    return token;
  }

  private String getRemoteAddress(HttpServletRequest req) {
    String remoteAddress = req.getRemoteAddr();
    if (!StringUtils.isEmpty(req.getHeader("X-Forwarded-For"))) {
      remoteAddress = req.getHeader("X-Forwarded-For");
    }
    return remoteAddress;
  }
}

