package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import com.jvm.coms4156.columbia.wehealth.entity.WeightHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightHistoryRepository extends JpaRepository<WeightHistory, Integer> {

  List<WeightHistory> findAllByUserOrderByCreatedTime(DbUser user);

  List<WeightHistory> findAllByUserAndCreatedTimeAfterOrderByCreatedTime(DbUser user, String startDateTime);

  Optional<WeightHistory> findByWeightHistoryId(Integer weightHistoryId);
}
