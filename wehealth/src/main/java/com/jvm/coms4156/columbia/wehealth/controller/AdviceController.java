package com.jvm.coms4156.columbia.wehealth.controller;

import com.jvm.coms4156.columbia.wehealth.dto.AdviceDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietRecordDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.service.AdviceService;
import com.jvm.coms4156.columbia.wehealth.service.DietService;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.ALL;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.ONE;

@RestController
@Log4j2
public class AdviceController extends BaseController {
    @Autowired
    private AdviceService adviceService;

    @GetMapping(path = "/diet/records", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DietHistoryResponseDto> getDietRecords (
            @RequestParam Optional<String> unit,
            @RequestParam Optional<Integer> length,
            @RequestBody UserIdDto userIdDto) {

        log.info("Get diet history in duration: {} {}", length.orElse(ONE), unit.orElse(ALL));
        AdviceDto adviceDto = adviceService.getAdvice(userIdDto);
        return new ResponseEntity<>(adviceDto, HttpStatus.OK);
    }



}
