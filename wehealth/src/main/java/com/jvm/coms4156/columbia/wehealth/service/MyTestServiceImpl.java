package com.jvm.coms4156.columbia.wehealth.service;

import com.jvm.coms4156.columbia.wehealth.dto.MyTestDto;
import com.jvm.coms4156.columbia.wehealth.entity.MyTest;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.repository.MyTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyTestServiceImpl implements MyTestService {

    @Autowired
    MyTestRepository myTestRepository;

    @Override
    public MyTestDto getMyTestInfo(int testId) {
        Optional<MyTest> myTest = myTestRepository.findByTestId(testId);

        if (myTest.isPresent()) {
            return new MyTestDto(myTest.get().getTestInfo());
        }
        else {
            throw new BadRequestException("Did not find test ID: " + testId);
        }
    }
}
