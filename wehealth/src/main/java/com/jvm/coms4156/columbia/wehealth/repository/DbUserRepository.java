package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbUserRepository extends JpaRepository<DbUser, Integer> {
  Optional<DbUser> findByUserId(Long userId);
}
