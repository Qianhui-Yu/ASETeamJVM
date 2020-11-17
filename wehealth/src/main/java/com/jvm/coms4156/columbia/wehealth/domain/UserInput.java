package com.jvm.coms4156.columbia.wehealth.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInput {
  private String username;
  private String currentPassword;
  private String newPassword;

  /**
   * Construct a UserInput object.
   *
   * @param username username.
   * @param currentPassword user's current password. For new user it should be same as newPassword.
   * @param newPassword new password to overwrite current password.
   */
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
