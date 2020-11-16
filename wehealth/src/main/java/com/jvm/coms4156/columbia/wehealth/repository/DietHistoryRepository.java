package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.entity.DietHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DietHistoryRepository extends JpaRepository<DietHistory, Integer> {
    List<DietHistory> findAllByUser(DBUser user);
    List<DietHistory> findAllByUserAndCreatedTimeAfter(DBUser user, String startDateTime);
    Optional<DietHistory> findByDietHistoryId(int dietHistoryId);
}
