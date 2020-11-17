package com.jvm.coms4156.columbia.wehealth.controller;

import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseRecordDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.service.ExerciseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Log4j2
public class ExerciseController extends BaseController {
    @Autowired
    private ExerciseService exerciseService;

    @PostMapping(path = "/exercise/records", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addExerciseRecord(@RequestBody ExerciseRecordDto exerciseRecordDto) {
        log.info("New Exercise Record: {}", exerciseRecordDto.toString());
        exerciseService.addExerciseRecordToDB(exerciseRecordDto);
        log.info("Successfully added a new exercise record.");
        return new ResponseEntity<>("Successfully recorded.", HttpStatus.OK);
    }

    @GetMapping(path = "/exercise/records", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExerciseHistoryResponseDto> getExerciseRecords(
            @RequestParam Optional<String> unit,
            @RequestParam Optional<Integer> length,
            @RequestBody UserIdDto userIdDto) {
        ExerciseHistoryResponseDto exerciseHistoryResponseDto = exerciseService.getExerciseHistory(userIdDto, unit, length);
        return new ResponseEntity<>(exerciseHistoryResponseDto, HttpStatus.OK);
    }

    @PutMapping(path = "/exercise/records/{recordId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editExerciseRecords(
            @PathVariable Optional<Integer> recordId,
            @RequestBody ExerciseRecordDto exerciseRecordDto) {
        log.info("Edit Exercise Record with id {}: {}", recordId, exerciseRecordDto.toString());
        exerciseService.editExerciseRecordAtDB(recordId, exerciseRecordDto);
        log.info("Successfully edit the record.");
        return new ResponseEntity<>("Successfully recorded.", HttpStatus.OK);
    }

    @DeleteMapping(path = "/exercise/records/{recordId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editExerciseRecords(
            @PathVariable Optional<Integer> recordId,
            @RequestBody UserIdDto userIdDto) {
        log.info("Delete Exercise Record with id {} for user {}", recordId, userIdDto.toString());
        exerciseService.deleteExerciseRecordInDB(recordId, userIdDto);
        log.info("Successfully edit the record.");
        return new ResponseEntity<>("Successfully recorded.", HttpStatus.OK);
    }
}
