package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.entity.WeightHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeightHistoryRepository extends JpaRepository<WeightHistory, Integer> {
    List<WeightHistory> findAllByUser(DBUser user);
    List<WeightHistory> findAllByUserAndCreatedTimeAfter(DBUser user, String startDateTime);
    Optional<WeightHistory> findByWeightHistoryId(String weightHistoryId);
}
