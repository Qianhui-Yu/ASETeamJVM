package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import com.jvm.coms4156.columbia.wehealth.entity.DietHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DietHistoryRepository extends JpaRepository<DietHistory, Integer> {
  List<DietHistory> findAllByUser(DbUser user);

  List<DietHistory> findAllByUserAndCreatedTimeAfter(DbUser user, String startDateTime);

  Optional<DietHistory> findByDietHistoryId(int dietHistoryId);
}
