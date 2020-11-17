package com.jvm.coms4156.columbia.wehealth.service;
import static org.mockito.Mockito.when;

import com.jvm.coms4156.columbia.wehealth.dto.DietRecordDto;
import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.entity.DietType;
import com.jvm.coms4156.columbia.wehealth.repository.DBUserRepository;
import com.jvm.coms4156.columbia.wehealth.repository.DietHistoryRepository;
import com.jvm.coms4156.columbia.wehealth.repository.DietTypeRepository;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DietServiceTest {

    @InjectMocks
    private DietService dietService;

    @Mock
    private DBUserRepository dbUserRepoMock;

    @Mock
    private DietHistoryRepository dietHistoryRepository;

    @Mock
    private DietTypeRepository dietTypeRepository;

    private DBUser validUser(Long userId) {
        DBUser dbUser = new DBUser("1", "1");
        dbUser.setUserId(userId);
        return dbUser;
    }

    @Test
    public void addDietToRecordTest(){
        DietRecordDto dietRecordDto = new DietRecordDto();

    }
//    public void getExerciseHistoryNegativeDurationTest() {
//        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
//        Assertions.assertThrows(BadRequestException.class, () -> {
//            exerciseService.getExerciseHistory(new UserIdDto(1L), Optional.empty(), Optional.of(-100));
//        });
//    }




}