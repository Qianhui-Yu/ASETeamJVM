package com.jvm.coms4156.columbia.wehealth.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Ethan Li on 11-04-2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field {

  private String name;
  private Object oldValue;
  private Object newValue;

  public static Field create(String name, Object newValue) {
    return new Field(name, null, newValue);
  }

  public static Field uip(String name, Object oldValue, Object newValue) {
    return new Field(name, oldValue, newValue == null ? oldValue : newValue);
  }
}
