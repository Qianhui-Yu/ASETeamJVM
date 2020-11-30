package com.jvm.coms4156.columbia.wehealth.controller;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.SUCCESS_MSG;

import com.jvm.coms4156.columbia.wehealth.dto.WeightHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightRecordDto;
import com.jvm.coms4156.columbia.wehealth.service.WeightService;
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
public class WeightController extends BaseController {
  @Autowired
  private WeightService weightService;

  /**
   * Hanlder for adding a weight record into the database.
   *
   * @param weightRecordDto Input weight record object. Refer to dto/WeightRecordDto for details.
   * @return Return 200 for success and 401 for unauthorized.
   */
  @PostMapping(path = "/weight/records",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> addWeightRecord(
          @RequestBody WeightRecordDto weightRecordDto) {
    log.info("New Weight Record: {}", weightRecordDto.toString());
    weightService.addWeightRecordToDb(au(), weightRecordDto);
    log.info("Successfully added a new weight record.");
    return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
  }

  /**
   * Handler for getting weight records based on input criteria.
   *
   * @param unit Unit of the time span. Among ["day", "week", "month", "year"]
   * @param length Number of units to data back from current date.
   * @return Return 200 for success, 400 for bad request (invalid user ID), and 401 for
   *         unauthorized access.
   */
  @GetMapping(path = "/weight/records",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<WeightHistoryResponseDto> getWeightRecords(
          @RequestParam Optional<String> unit,
          @RequestParam Optional<Integer> length) {
    WeightHistoryResponseDto weightHistoryResponseDto = weightService
            .getWeightHistory(au(), unit, length);
    return new ResponseEntity<>(weightHistoryResponseDto, HttpStatus.OK);
  }

  /**
   * Hanlder for editing a weight record.
   *
   * @param weightId ID of the record to be edited.
   * @param weightRecordDto Target weight record after editing.
   * @return Return 200 for success, 400 for bad request (invalid user ID), and 401 for
   *         unauthorized access.
   */
  @PutMapping(path = "/weight/records/{weightId}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> editWeightRecord(
          @PathVariable Integer weightId,
          @RequestBody WeightRecordDto weightRecordDto) {
    log.info("Edit Weight Record ID: {}. Result Record: {}", weightId, weightRecordDto.toString());
    weightService.editWeightRecord(au(), weightId, weightRecordDto);
    log.info("Successfully edited weight record {}.", weightId);
    return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
  }

  /**
   * Hanlder for deleting a weight record from the database.
   *
   * @param weightId ID of the weight record to be deleted.
   * @return Return 200 for success, 400 for bad request (invalid user ID), and 401 for
   *         unauthorized access.
   */
  @DeleteMapping(path = "weight/records/{weightId}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> deleteWeightRecord(
          @PathVariable Integer weightId) {
    log.info("Delete Weight Record ID: {}.", weightId);
    weightService.deleteWeightRecord(au(), weightId);
    log.info("Successfully deleted weight record {}.", weightId);
    return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
  }

}