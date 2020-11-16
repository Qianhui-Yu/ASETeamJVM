package com.jvm.coms4156.columbia.wehealth.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

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
    private DBUser user;

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
