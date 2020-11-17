package com.jvm.coms4156.columbia.wehealth.entity;

import com.jvm.coms4156.columbia.wehealth.utility.Utility;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity(name = "user")
@NoArgsConstructor
@Data
public class DbUser implements Serializable {

  public static final int ADMIN = 1;
  public static final int USER = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id", unique = true)
  private Long userId;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "user_type", nullable = false)
  private int userType;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "salt", nullable = false)
  private String salt;

  @Column(name = "lookup_token")
  private String lookupToken;

  @Column(name = "created_time", nullable = false)
  private String createdTime;

  @Column(name = "updated_time")
  private String updateTime;

  public DbUser(String username, String lookupToken) {
    this.username = username;
    this.lookupToken = lookupToken;
    this.createdTime = Utility.getStringOfCurrentDateTime();
  }

  public void setPassword(String clearTextPassword) {
    salt = UUID.randomUUID().toString();
    passwordHash = new BCryptPasswordEncoder().encode(salt + "&&" + clearTextPassword);
  }

}