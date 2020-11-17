package com.jvm.coms4156.columbia.wehealth.controller;

import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietRecordDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.service.DietService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Log4j2
public class DietController {
    @Autowired
    private DietService dietService;

    @PostMapping(path = "/diet/records", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addDietRecord(@RequestBody DietRecordDto dietRecordDto) {
        log.info("New Diet Record: {}", dietRecordDto.toString());
        dietService.addDietRecordToDB(dietRecordDto);
        log.info("Successfully added a new diet record.");
        return new ResponseEntity<>("Successfully recorded.", HttpStatus.OK);
    }

    @GetMapping(path = "/diet/records", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DietHistoryResponseDto> getDietRecords(
            @RequestParam Optional<String> unit,
            @RequestParam Optional<Integer> length,
            @RequestBody UserIdDto userIdDto) {
        DietHistoryResponseDto dietHistoryResponseDto = dietService.getDietHistory(userIdDto, unit, length);
        return new ResponseEntity<>(dietHistoryResponseDto, HttpStatus.OK);
    }


}
