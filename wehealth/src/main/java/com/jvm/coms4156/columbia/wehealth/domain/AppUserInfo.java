package com.jvm.coms4156.columbia.wehealth.domain;

import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
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
  private long userId;
  private String username;
  private int userType;
  private String lookupToken;

  public AppUserInfo(DbUser user) {

    this(user.getUserId(), user.getUsername(), user.getUserType(), user.getLookupToken());
  }
}
