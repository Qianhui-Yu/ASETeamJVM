package com.jvm.coms4156.columbia.wehealth.service;
import static org.mockito.Mockito.when;

import com.jvm.coms4156.columbia.wehealth.dto.DietRecordDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.entity.DietHistory;
import com.jvm.coms4156.columbia.wehealth.entity.DietType;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DietServiceTest {

    @InjectMocks
    private DietService dietService;

    @Mock
    private DBUserRepository dbUserRepoMock;

    @Mock
    private DietHistoryRepository dietHistoryRepoMock;

    @Mock
    private DietTypeRepository dietTypeRepoMock;

    @Mock
    private NutrientTypeRepository nutrientTypeRepoMock;

    @Mock
    private DietNutrientMappingRepository dietNutrientMappingRepoMock;

    private DBUser validUser(Long userId) {
        DBUser dbUser = new DBUser("1", "1");
        dbUser.setUserId(userId);
        return dbUser;
    }

    private DietType validDiet(Integer dietTypeId){
        DietType dietType = new DietType();
        dietType.setDietTypeId(dietTypeId);
        dietType.setDietTypeName("test");
        return dietType;
    }

    private DietHistory validDietHistory(Integer diet_history_id){
        DietHistory dietHistory = new DietHistory();
        dietHistory.setDietHistoryId(diet_history_id);
        dietHistory.setDietType(validDiet(1));
        dietHistory.setUser(validUser(1L));
        return dietHistory;
    }

    @Test
    public void addDietTypeTest(){
        dietService.addDietType(1, "test_diet");
    }

    @Test
    public void addDietRecordValidTest(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
        when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class))).thenReturn(Optional.of(validDiet(1)));
        DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0, "gram", 10.0, 10.0, 10.0, 10.0);
        dietService.addDietRecordToDB(dietRecordDto);
    }

    @Test
    public void addDietRecordInvalidTest(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
        when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class))).thenReturn(Optional.of(validDiet(1)));
        DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0, "gram", 10.0, 10.0, 10.0, 10.0);
        Assertions.assertThrows(NotFoundException.class, () -> {
            dietService.addDietRecordToDB(dietRecordDto);
        });
    }

    @Test
    public void addDietRecordInvalid2Test(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
        when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0, "gram", 10.0, 10.0, 10.0, 10.0);
        Assertions.assertThrows(BadRequestException.class, () -> {
            dietService.addDietRecordToDB(dietRecordDto);
        });
    }

    @Test
    public void addDietNutrientMappingValidTest(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
        when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class))).thenReturn(Optional.of(validDiet(1)));
        DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0, "gram", 10.0, 10.0, 10.0, 10.0);
        dietService.addAllNutrientsInfoToDietNutrientMapping(dietRecordDto);
    }

    @Test
    public void addDietNutrientMappingInvalidTest(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
        when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0, "gram", 10.0, 10.0, 10.0, 10.0);
        Assertions.assertThrows(BadRequestException.class, () -> {
            dietService.addDietRecordToDB(dietRecordDto);
        });
    }

    @Test
    public void getDietHistoryValid(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
        UserIdDto userIdDto = new UserIdDto(1L);
        String unit = "all";
        Integer length = 1;
        dietService.getDietHistory(userIdDto, Optional.of(unit), Optional.of(length));
    }

    @Test
    public void getDietHistoryInvalidTest(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
        UserIdDto userIdDto = new UserIdDto(1L);
        String unit = "all";
        Integer length = 1;
        Assertions.assertThrows(BadRequestException.class, () -> {
            dietService.getDietHistory(userIdDto, Optional.of(unit), Optional.of(length));
        });
    }

    @Test
    public void getDietHistoryInvalid2Test(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
        UserIdDto userIdDto = new UserIdDto(1L);
        String unit = "all";
        Integer length = -1;
        Assertions.assertThrows(BadRequestException.class, () -> {
            dietService.getDietHistory(userIdDto, Optional.of(unit), Optional.of(length));
        });
    }

    @Test
    public void updateDietHistoryTest(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
        when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class))).thenReturn(Optional.of(validDietHistory(1)));
        DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0, "gram", 10.0, 10.0, 10.0, 10.0);
        dietService.updateDietHistory(1, dietRecordDto);
    }

    @Test
    public void updateDietHistoryInvalidTest(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
        when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class))).thenReturn(Optional.of(validDietHistory(1)));
        DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0, "gram", 10.0, 10.0, 10.0, 10.0);
        Assertions.assertThrows(BadRequestException.class, () -> {
            dietService.updateDietHistory(1, dietRecordDto);
        });

    }

    @Test
    public void updateDietHistoryInvalid2Test(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
        when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0, "gram", 10.0, 10.0, 10.0, 10.0);
        Assertions.assertThrows(BadRequestException.class, () -> {
            dietService.updateDietHistory(1, dietRecordDto);
        });

    }


    @Test
    public void deleteDietHistoryValidTest(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
        when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class))).thenReturn(Optional.of(validDietHistory(1)));
        UserIdDto userIdDto = new UserIdDto(1L);
        dietService.deleteDietHistory(1, userIdDto);
    }

    @Test
    public void deleteDietHistoryInvalidTest(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
        when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        UserIdDto userIdDto = new UserIdDto(1L);
        Assertions.assertThrows(BadRequestException.class, () -> {
            dietService.deleteDietHistory(1, userIdDto);
        });
    }

    @Test
    public void deleteDietHistoryInvalid2Test(){
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
        when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        UserIdDto userIdDto = new UserIdDto(1L);
        Assertions.assertThrows(BadRequestException.class, () -> {
            dietService.deleteDietHistory(1, userIdDto);
        });
    }





}