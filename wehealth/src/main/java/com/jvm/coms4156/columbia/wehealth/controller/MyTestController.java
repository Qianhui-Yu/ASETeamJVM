package com.jvm.coms4156.columbia.wehealth.controller;

import com.jvm.coms4156.columbia.wehealth.dto.MyTestDto;
import com.jvm.coms4156.columbia.wehealth.service.MyTestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class MyTestController {
    @Autowired
    MyTestService myTestService;

    @GetMapping(path = "/test/{testId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyTestDto> getMyTestInfoByMyTestId(
            @PathVariable int testId
    ) {
        log.info("Get TestID: " + testId + "'s Info...");
        MyTestDto result = myTestService.getMyTestInfo(testId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

//    @GetMapping(path = "/test/{testId}/extra")
//    public ResponseEntity<String> getPathVariable(
//            @PathVariable Integer testId,
//            @RequestParam String extra
//    ){
//        String result = "testId = " + testId + " extra = " + extra;
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
}
