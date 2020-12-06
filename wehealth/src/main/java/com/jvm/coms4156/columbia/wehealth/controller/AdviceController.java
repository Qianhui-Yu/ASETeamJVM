package com.jvm.coms4156.columbia.wehealth.controller;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.MONTH;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.ONE;

import com.jvm.coms4156.columbia.wehealth.dto.AdviceDto;
import com.jvm.coms4156.columbia.wehealth.service.AdviceService;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
   * @param unit indicate the time unit of data source for the advice.
   * @param length indicate the time length of data source for the advice.
   * @return Return 200 for success, 400 for bad request, and 401 for
   *         unauthorized access.
   */
  @GetMapping(path = "/advice", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdviceDto> getDietRecords(
          @RequestParam Optional<String> unit,
          @RequestParam Optional<Integer> length) {

    log.info("************Get advice {} {} {}***********", au().getUsername(),
            length.orElse(ONE), unit.orElse(MONTH));

    AdviceDto adviceDto = adviceService.getAdvice(au(), length, unit);
    return new ResponseEntity<>(adviceDto, HttpStatus.OK);
  }



}
