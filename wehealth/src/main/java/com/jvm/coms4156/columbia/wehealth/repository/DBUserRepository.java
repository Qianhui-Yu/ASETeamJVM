//package com.jvm.coms4156.columbia.wehealth.repository;
//
//
//import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface DBUserRepository extends JpaRepository<DBUser, Integer> {
//
//  @Query("SELECT * FROM USER where user_id = ?1")
//  Optional<DBUser> findByUserId(int userId);
//
//
//}
