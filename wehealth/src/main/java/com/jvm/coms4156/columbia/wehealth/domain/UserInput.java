package com.jvm.coms4156.columbia.wehealth.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInput {
  private String username;
  private String currentPassword;
  private String newPassword;

  public UserInput(String username, String currentPassword, String newPassword) {
    this.username = username;
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
  }

  public UserInput(String username, String currentPassword) {
    this.username = username;
    this.currentPassword = currentPassword;
  }
}
