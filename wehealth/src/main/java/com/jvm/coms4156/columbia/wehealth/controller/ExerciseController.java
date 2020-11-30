package com.jvm.coms4156.columbia.wehealth.controller;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.SUCCESS_MSG;

import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseRecordDto;
import com.jvm.coms4156.columbia.wehealth.service.ExerciseService;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@Log4j2
public class ExerciseController extends BaseController {
  @Autowired
  private ExerciseService exerciseService;

  /**
   * API handler for creating one exercise record.
   *
   * @param exerciseRecordDto dto that contains the record information
   * @return response entity that contains result of the operation
   */
  @PostMapping(path = "/exercise/records", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> addExerciseRecord(
          @RequestBody ExerciseRecordDto exerciseRecordDto) {
    log.info("New Exercise Record: {}", exerciseRecordDto.toString());
    exerciseService.addExerciseRecordToDb(exerciseRecordDto, au());
    log.info("Successfully added a new exercise record.");
    return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
  }

  /**
   * API handler for getting history of exercise.
   *
   * @param unit Unit of time for specific period of history.
   *             If ALL or empty, retrieve all history.
   * @param length Length of time to retrieve, Ignored if unit is ALL.
   * @return response entity that contains result of the operation and retrieved history
   */
  @GetMapping(path = "/exercise/records", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ExerciseHistoryResponseDto> getExerciseRecords(
          @RequestParam Optional<String> unit,
          @RequestParam Optional<Integer> length) {
    ExerciseHistoryResponseDto exerciseHistoryResponseDto = exerciseService
            .getExerciseHistory(unit, length, au());
    return new ResponseEntity<>(exerciseHistoryResponseDto, HttpStatus.OK);
  }

  /**
   * API handler for editing one exercise record in the database.
   *
   * @param recordId the id of the record to be edited.
   * @param exerciseRecordDto the content of the result to edit to.
   * @return response entity that contains result of the operation
   */
  @PutMapping(path = "/exercise/records/{recordId}", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> editExerciseRecords(
          @PathVariable Optional<Integer> recordId,
          @RequestBody ExerciseRecordDto exerciseRecordDto) {
    log.info("Edit Exercise Record with id {}: {}", recordId, exerciseRecordDto.toString());
    exerciseService.editExerciseRecordAtDb(recordId, exerciseRecordDto, au());
    log.info("Successfully edit the record.");
    return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
  }

  /**
   * API handler for deleting one exercise record from database.
   *
   * @param recordId the id of the record to be deleted.
   * @return response entity that contains result of the operation
   */
  @DeleteMapping(path = "/exercise/records/{recordId}", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> editExerciseRecords(
          @PathVariable Optional<Integer> recordId) {
    log.info("Delete Exercise Record with id {} for user {}", recordId, au().getUsername());
    exerciseService.deleteExerciseRecordInDb(recordId, au());
    log.info("Successfully edit the record.");
    return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
  }
}
