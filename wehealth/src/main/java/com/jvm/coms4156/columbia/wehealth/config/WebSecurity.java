package com.jvm.coms4156.columbia.wehealth.config;

import com.jvm.coms4156.columbia.wehealth.service.JwtService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;


  @Autowired
  public WebSecurity(AuthenticationManager authenticationManager, JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/api/appUser/**").permitAll()
        .antMatchers(HttpMethod.GET,  "/diet/**").permitAll()
        .antMatchers(HttpMethod.PUT,  "/diet/**").permitAll()
        .antMatchers(HttpMethod.POST,  "/diet/**").permitAll()
        .antMatchers(HttpMethod.DELETE,  "/diet/**").permitAll()
        .anyRequest().fullyAuthenticated()
        .and()
        .addFilter(new SecurityFilter(jwtService))
        .addFilter(new BasicAuthenticationFilter(authenticationManager) {
          @Override
          protected void doFilterInternal(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain) throws IOException, ServletException {
            chain.doFilter(request, response);
          }
        })
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    ;
  }

}


