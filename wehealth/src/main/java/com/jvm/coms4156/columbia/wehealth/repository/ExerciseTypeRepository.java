package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.ExerciseType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Integer> {
  //Optional<ExerciseType> findByExerciseTypeId(int exerciseTypeId);
  Optional<ExerciseType> findByExerciseTypeName(String exerciseTypeName);
}
