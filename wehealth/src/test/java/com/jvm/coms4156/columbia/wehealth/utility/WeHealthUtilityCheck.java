package com.jvm.coms4156.columbia.wehealth.utility;

import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import com.jvm.coms4156.columbia.wehealth.utility.Utility;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeHealthUtilityCheck {
    @Test
    public void getStringFromDateTest() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String testDateString = "2020-11-16 11:11:11";
            Date testDate = df.parse(testDateString);
            Assertions.assertEquals(true,
                    testDateString.equals(Utility.getStringFromDate(testDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /* TODO(Derek Jin): using Date() to get current time makes it difficult to test
            Consider changing implementation to use java.time.Clock
     */
    @Test
    public void getStringOfCurrentDateTimeTest() {
        Utility.getStringOfCurrentDateTime();
    }

    @Test
    public void getStringOfStartDateTimeInvalidUnit() {
        Assertions.assertThrows(BadRequestException.class, () -> {
           Utility.getStringOfStartDateTime("BadTestUnit", 1);
        });
    }

    @Test
    public void getStringOfStartDateTimeYear() {
        Utility.getStringOfStartDateTime(YEAR, 1);
    }

    @Test
    public void getStringOfStartDateTimeWeek() {
        Utility.getStringOfStartDateTime(WEEK, 1);
    }

    @Test
    public void getStringOfStartDateTimeMonth() {
        Utility.getStringOfStartDateTime(MONTH, 1);
    }

}