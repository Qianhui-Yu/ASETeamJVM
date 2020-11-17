package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.DietType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietTypeRepository extends JpaRepository<DietType, Integer> {
  Optional<DietType> findByDietTypeId(int dietTypeId);
}
