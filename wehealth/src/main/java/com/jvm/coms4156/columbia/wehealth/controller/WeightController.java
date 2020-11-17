package com.jvm.coms4156.columbia.wehealth.controller;

import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightRecordDto;
import com.jvm.coms4156.columbia.wehealth.service.WeightService;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class WeightController {
  @Autowired
  private WeightService weightService;

  @PostMapping(path = "/weight/records",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> addWeightRecord(@RequestBody WeightRecordDto weightRecordDto) {
    log.info("New Weight Record: {}", weightRecordDto.toString());
    weightService.addWeightRecordToDb(weightRecordDto);
    log.info("Successfully added a new weight record.");
    return new ResponseEntity<>("Successfully recorded.", HttpStatus.OK);
  }

  @GetMapping(path = "/weight/records",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<WeightHistoryResponseDto> getWeightRecords(
          @RequestParam Optional<String> unit,
          @RequestParam Optional<Integer> length,
          @RequestBody UserIdDto userIdDto) {
    WeightHistoryResponseDto weightHistoryResponseDto = weightService
            .getWeightHistory(userIdDto, unit, length);
    return new ResponseEntity<>(weightHistoryResponseDto, HttpStatus.OK);
  }

  @PutMapping(path = "/weight/records/{weightId}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> editWeightRecord(
          @PathVariable Integer weightId,
          @RequestBody WeightRecordDto weightRecordDto) {
    log.info("Edit Weight Record ID: {}. Result Record: {}", weightId, weightRecordDto.toString());
    weightService.editWeightRecord(weightId, weightRecordDto);
    log.info("Successfully edited weight record {}.", weightId);
    return new ResponseEntity<>("Successfully recorded.", HttpStatus.OK);
  }

  @DeleteMapping(path = "weight/records/{weightId}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> deleteWeightRecord(
          @PathVariable Integer weightId,
          @RequestBody UserIdDto userIdDto) {
    log.info("Delete Weight Record ID: {}.", weightId);
    weightService.deleteWeightRecord(weightId, userIdDto);
    log.info("Successfully deleted weight record {}.", weightId);
    return new ResponseEntity<>("Successfully recorded.", HttpStatus.OK);
  }

}