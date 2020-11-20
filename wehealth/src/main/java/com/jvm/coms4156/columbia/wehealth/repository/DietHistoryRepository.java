package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import com.jvm.coms4156.columbia.wehealth.entity.DietHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface DietHistoryRepository extends JpaRepository<DietHistory, Integer> {
  List<DietHistory> findAllByUser(DbUser user);

  List<DietHistory> findAllByUserAndCreatedTimeAfter(DbUser user, String startDateTime);

  Optional<DietHistory> findByDietHistoryId(int dietHistoryId);

//  @Query("SELECT created_date, nutrient_type_name, SUM(value * weight / 100) as total_value\n" +
//          "FROM (SELECT nutrient_type_name, value, weight, user_id, CAST(diet_history.created_time AS DATE) as created_date" +
//          "FROM diet_history JOIN diet_nutrient_mapping ON diet_history.diet_type_id = diet_nutrient_mapping.diet_type_id\n" +
//          "JOIN nutrient_type ON nutrient_type.nutrient_type_id = nutrient_type.nutrient_type_id\n" +
//          "WHERE diet_history.user_id = 54) as nutrients GROUP BY nutrient_type_name, created_date;\n")
//  Optional<DietStats> findDietStatsByUserId(Long userId);
}
