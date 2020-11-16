package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.NutrientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NutrientTypeRepository extends JpaRepository<NutrientType, Integer> {
    NutrientType findByNutrientTypeId(int nutrientTypeId);
}
