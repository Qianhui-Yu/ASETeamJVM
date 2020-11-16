package com.jvm.coms4156.columbia.wehealth.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(schema = "wehealth", name = "exercise_history")
@Data
public class ExerciseHistory {
    @Id
    @Column(name = "exercise_history_id")
    private int exerciseHistoryId;

    @JoinColumn(name = "exercise_type_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ExerciseType exerciseType;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private DBUser user;

    @Column(name = "duration_in_mins")
    private double DurationInMins;

    @Column(name = "created_time")
    private String createdTime;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_time")
    private String updatedTime;

    @Column(name = "updated_by")
    private String updatedBy;
}
