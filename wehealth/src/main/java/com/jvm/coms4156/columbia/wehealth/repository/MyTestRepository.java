package com.jvm.coms4156.columbia.wehealth.repository;

import com.jvm.coms4156.columbia.wehealth.entity.MyTest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyTestRepository extends JpaRepository<MyTest, Integer> {

  Optional<MyTest> findByTestId(int testId);

}
