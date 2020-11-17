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
  private long userId;
  private int userType;
  private String username;

  public AuthenticatedUser(long userId, int userType, String username) {
    super(null, buildGrantedAuthority(userType));
    this.username = username;
    this.userId = userId;
    this.userType = userType;
  }

  public AuthenticatedUser(long userId, int userType) {
    super(null, buildGrantedAuthority(userType));
    this.userId = userId;
    this.userType = userType;
  }

  public AuthenticatedUser(long userId) {
    super("Test User", "Test Credentials");
    this.userId = userId;
  }

  private static Collection<? extends GrantedAuthority> buildGrantedAuthority(int userType) {
    List<GrantedAuthority> out = new ArrayList<>();
    out.add(new SimpleGrantedAuthority("ROLE_USER"));
    if (userType == DbUser.ADMIN) {
      out.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    return out;
  }
}
