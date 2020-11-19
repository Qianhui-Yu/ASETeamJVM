package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import com.jvm.coms4156.columbia.wehealth.entity.ExerciseHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseHistoryRepository extends JpaRepository<ExerciseHistory, Integer> {

  List<ExerciseHistory> findAllByUser(DbUser user);

  List<ExerciseHistory> findAllByUserAndCreatedTimeAfter(DbUser user, String startDateTime);

  Optional<ExerciseHistory> findByExerciseHistoryId(Integer exerciseHistoryId);
}
