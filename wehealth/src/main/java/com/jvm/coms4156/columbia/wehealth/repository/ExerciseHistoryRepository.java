package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.entity.ExerciseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseHistoryRepository extends JpaRepository<ExerciseHistory, Integer> {
    List<ExerciseHistory> findAllByUser(DBUser user);
    List<ExerciseHistory> findAllByUserAndCreatedTimeAfter(DBUser user, String startDateTime);
    Optional<ExerciseHistory> findByExerciseHistoryId(Integer exerciseHistoryId);
}
