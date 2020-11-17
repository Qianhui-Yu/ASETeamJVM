package com.jvm.coms4156.columbia.wehealth.service;

import com.jvm.coms4156.columbia.wehealth.dto.AdviceDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AdviceService {

    public AdviceDto getAdvice(UserIdDto userIdDto){
        AdviceDto adviceDto = new AdviceDto();
        return adviceDto;
    }
}
