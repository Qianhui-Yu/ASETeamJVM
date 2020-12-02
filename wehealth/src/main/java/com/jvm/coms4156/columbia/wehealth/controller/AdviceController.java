package com.jvm.coms4156.columbia.wehealth.controller;

import com.jvm.coms4156.columbia.wehealth.dto.AdviceDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.service.AdviceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@Log4j2
public class AdviceController extends BaseController {
  @Autowired
  private AdviceService adviceService;

  /**
   * Hanlder for getting advice.
   *
   * @param userIdDto indicate the user who wants advice.
   * @return Return 200 for success, 400 for bad request (invalid diet history ID), and 401 for
   *         unauthorized access.
   */
  @GetMapping(path = "/advice", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdviceDto> getDietRecords() {
    log.info("Get advice {}", au().toString());
    AdviceDto adviceDto = adviceService.getAdvice(au());
    return new ResponseEntity<>(adviceDto, HttpStatus.OK);
  }



}
