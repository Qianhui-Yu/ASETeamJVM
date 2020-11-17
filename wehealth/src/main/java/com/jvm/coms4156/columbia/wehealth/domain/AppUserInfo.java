package com.jvm.coms4156.columbia.wehealth.domain;

import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Ethan on 11/04/2020.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserInfo {
  private long user_id;
  private String username;
  private int user_type;

  public AppUserInfo(DBUser user) {
    this(user.getUserId(), user.getUsername(), user.getUser_type());
  }
}
