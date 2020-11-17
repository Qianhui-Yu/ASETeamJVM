package com.jvm.coms4156.columbia.wehealth.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(schema = "wehealth", name = "diet_history")
@Data
public class DietHistory implements Serializable {
  @Id
  @Column(name = "diet_history_id")
  private int dietHistoryId;

  @JoinColumn(name = "diet_type_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private DietType dietType;

  @JoinColumn(name = "user_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private DbUser user;

  @Column(name = "weight")
  private double weight;

  @Column(name = "unit")
  private String unit;

  @Column(name = "created_time")
  private String createdTime;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_time")
  private String updatedTime;

  @Column(name = "updated_by")
  private String updatedBy;
}
