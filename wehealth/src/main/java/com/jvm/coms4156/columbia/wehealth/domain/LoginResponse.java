package com.jvm.coms4156.columbia.wehealth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by emagi on 7/31/2019.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  private AppUserInfo userInfo;
  private String token;
  private long tokenExpiration;

}
