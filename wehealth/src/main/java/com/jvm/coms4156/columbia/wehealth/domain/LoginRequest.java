package com.jvm.coms4156.columbia.wehealth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Ethan on 11/04/2020.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

  private String username;
  private String password;

}
