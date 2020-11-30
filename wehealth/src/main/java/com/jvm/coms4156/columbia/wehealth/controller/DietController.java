package com.jvm.coms4156.columbia.wehealth.controller;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.ALL;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.ONE;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.SUCCESS_MSG;

import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietRecordDto;
import com.jvm.coms4156.columbia.wehealth.service.DietService;
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
public class DietController extends BaseController {
  @Autowired
  private DietService dietService;

  /**
   * Handler for adding diet records into the database.
   *
   * @param dietRecordDto Input diet record object. Refer to dto/DietRecordDto for details.
   * @return Return 200 for success and 401 for unauthorized.
   */
  @PostMapping(path = "/diet/records", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> addDietRecord(@RequestBody DietRecordDto dietRecordDto) {
    log.info("New Diet Record: {}", dietRecordDto.toString());
    dietService.addDietRecordToDb(au(), dietRecordDto);

    log.info("Successfully added a new diet record.");
    return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
  }

  /**
   * Hanlder for getting a diet record.
   *
   * @param unit Span unit. Can be among ["day", "week", "month", "year"].
   * @param length Length of the given unit. Must be positive.
   * @return Return 200 for success, 400 for bad request (invalid user ID), and 401 for
   *         unauthorized access.
   */
  @GetMapping(path = "/diet/records", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DietHistoryResponseDto> getDietRecords(
          @RequestParam Optional<String> unit,
          @RequestParam Optional<Integer> length) {
    log.info("Get diet history in duration: username {} {} {}", au().getUsername(),
            length.orElse(ONE), unit.orElse(ALL));

    DietHistoryResponseDto dietHistoryResponseDto = dietService.getDietHistory(
            au(), unit, length);
    return new ResponseEntity<>(dietHistoryResponseDto, HttpStatus.OK);
  }

  /**
   * Handler for editing a diet record.
   *
   * @param recordId ID of the record to be edited.
   * @param dietRecordDto Input desired diet record object. Refer to dto/DietRecordDto for details.
   * @return Return 200 for success, 400 for bad request (invalid diet history ID), and 401 for
   *         unauthorized access.
   */
  @PutMapping(path = "/diet/records/{recordId}", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateDietRecord(@PathVariable Integer recordId,
                                                 @RequestBody DietRecordDto dietRecordDto) {
    log.info("Updating diet record {}", recordId);
    dietService.updateDietHistory(au(), recordId, dietRecordDto);
    log.info("Successfully updated diet record {}", recordId);
    return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
  }

  /**
   * Hanlder for deleting a diet record.
   *
   * @param recordId ID of the record to be deleted.
   * @return Return 200 for success, 400 for bad request (invalid diet history ID), and 401 for
   *         unauthorized access.
   */
  @DeleteMapping(path = "/diet/records/{recordId}", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> deleteDietRecord(@PathVariable Integer recordId) {
    log.info("Deleting diet record {}", recordId);
    dietService.deleteDietHistory(au(), recordId);
    log.info("Successfully deleted diet record {}", recordId);
    return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
  }

}
