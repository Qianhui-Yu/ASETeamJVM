package com.jvm.coms4156.columbia.wehealth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(schema = "wehealth", name = "weight_history")
@Data
public class WeightHistory {
  @Id
  @Column(name = "weight_history_id")
  private int weightHistoryId;

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
