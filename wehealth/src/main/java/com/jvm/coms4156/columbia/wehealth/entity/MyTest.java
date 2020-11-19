package com.jvm.coms4156.columbia.wehealth.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(schema = "test_schema", name = "test")
@Data
public class MyTest implements Serializable {
  @Id
  @Column(name = "test_id")
  private int testId;

  @Column(name = "test_info")
  private String testInfo;
}
