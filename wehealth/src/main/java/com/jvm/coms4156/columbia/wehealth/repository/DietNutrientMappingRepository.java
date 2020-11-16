package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.DietNutrientMapping;
import com.jvm.coms4156.columbia.wehealth.entity.DietType;
import com.jvm.coms4156.columbia.wehealth.entity.NutrientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DietNutrientMappingRepository extends JpaRepository<DietNutrientMapping, Integer> {
    List<DietNutrientMapping> findAllByDietTypeOrderByNutrientType(DietType dietType);
    Optional<DietNutrientMapping> findByDietTypeAndNutrientType(DietType dietType, NutrientType nutrientType);
}
