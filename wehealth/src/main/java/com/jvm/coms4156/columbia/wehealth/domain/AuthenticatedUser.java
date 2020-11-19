package com.jvm.coms4156.columbia.wehealth.domain;


import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


/**
 * Created by Ethan on 11/04/2020.
 */
@Data
public class AuthenticatedUser extends UsernamePasswordAuthenticationToken {
  private Long userId;
  private int userType;
  private String username;

  /**
   * AuthenticatedUser constructor.
   *
   * @param userId Long
   * @param userType int
   * @param username String
   */
  public AuthenticatedUser(Long userId, int userType, String username) {
    super(username,null, buildGrantedAuthority(userType));
    this.username = username;
    this.userId = userId;
    this.userType = userType;
  }

  /**
   * AuthenticatedUser constructor.
   *
   * @param userId Long
   * @param userType int
   */
  public AuthenticatedUser(Long userId, int userType) {
    super("Test User", "Test credential");
    this.userId = userId;
    this.userType = userType;
  }

  /**
   * AuthenticatedUser constructor.
   *
   * @param userId Long
   */
  public AuthenticatedUser(Long userId) {
    super("Test User", "Test credential");
    this.userId = userId;
    this.username = "TestUsername";
  }

  /**
   * Grant authority to user.
   *
   * @param userType int
   * @return collection
   */
  private static Collection<? extends GrantedAuthority> buildGrantedAuthority(int userType) {
    List<GrantedAuthority> out = new ArrayList<>();
    out.add(new SimpleGrantedAuthority("ROLE_USER"));
    if (userType == 1) {
      out.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    return out;
  }
}
