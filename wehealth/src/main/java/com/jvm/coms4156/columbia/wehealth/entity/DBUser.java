package com.jvm.coms4156.columbia.wehealth.entity;

import java.util.UUID;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.jvm.coms4156.columbia.wehealth.Utility.Utility;
@Entity( name = "user")
@NoArgsConstructor
@Data
public class DBUser {

  public static final int ADMIN = 1;
  public static final int USER = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id", unique = true)
  private Long user_id;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "user_type", nullable = false)
  private int user_type;

  @Column(name = "password_hash", nullable = false)
  private String password_hash;

  @Column(name = "salt", nullable = false)
  private String salt;

  @Column(name = "lookup_token", nullable = false)
  private String lookup_token;

  @Column(name = "created_time", nullable = false)
  private String createdTime;

  @Column(name = "updated_time")
  private String updateTime;

  public DBUser( String username,  String lookup_token) {
    this.username = username;
    this.lookup_token = lookup_token;
    createdTime = Utility.getStringOfCurrentDateTime();
  }

  public void setPassword(String clearTextPassword) {
    salt = UUID.randomUUID().toString();
    password_hash = new BCryptPasswordEncoder().encode(salt + "&&" + clearTextPassword);
  }

}
