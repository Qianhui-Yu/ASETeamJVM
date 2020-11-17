package com.jvm.coms4156.columbia.wehealth.service;

import com.github.javafaker.Faker;
import com.jvm.coms4156.columbia.wehealth.dao.AppUserDao;
import com.jvm.coms4156.columbia.wehealth.domain.AppUserInfo;
import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.domain.LoginRequest;
import com.jvm.coms4156.columbia.wehealth.domain.LoginResponse;
import com.jvm.coms4156.columbia.wehealth.domain.UserInput;
import com.jvm.coms4156.columbia.wehealth.exception.DuplicateException;
import com.jvm.coms4156.columbia.wehealth.exception.MissingDataException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import org.springframework.beans.factory.annotation.Autowired;


import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;


/**
 * Created by Ethan 11/15/2020
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppUserServiceTest {
  @Autowired
  private JwtService jwtService;

  @Autowired
  private AppUserService appUserService;

  @Autowired
  private AppUserDao appUserDao;

  @Test
  public void testRegister() throws Exception {

    Faker faker = new Faker();
    String name = faker.name().fullName();

    AppUserInfo testUserInfo = appUserService.register(new UserInput(name, "123456", "123456"));

    assertEquals(0, testUserInfo.getUser_type());
    assertEquals(name, testUserInfo.getUsername());

    assertNotNull(testUserInfo.getLookupToken());
    LoginResponse resp = appUserService.verifyUser(testUserInfo.getLookupToken());
    System.out.println("response token: "+resp.getToken());

    assertNotNull(resp.getToken());
    AuthenticatedUser au = jwtService.verify(resp.getToken());


    String username = appUserDao.findByUserId(au.getUserId()).getUsername();
    assertEquals(name, username);
    assertEquals(0, au.getUserType());

  }

  @Test(expected = DuplicateException.class)
  public void registerTestExistingUser() throws Exception {
    appUserService.register(new UserInput( "testUsername", "1", "1"));
    appUserService.register(new UserInput( "testUsername", "1", "1"));
  }

  @Test(expected = MissingDataException.class)
  public void registerTestMissingUsername() throws Exception {
    appUserService.register(new UserInput( "", "1", "1"));
  }

  @Test(expected = MissingDataException.class)
  public void registerTestMissingPassword() throws Exception {
    Faker faker = new Faker();
    String name = faker.name().fullName();
    appUserService.register(new UserInput( name, "", ""));
  }

  @Test
  public void loginTest() throws Exception {
    LoginResponse resp = appUserService.login(new LoginRequest("Test1", "123456"));
    assertNotNull(resp);
  }

  @Test
  public void loginTestWithWrongPassword() throws Exception {
    LoginResponse resp = appUserService.login(new LoginRequest("Test1", "1"));
    assertNull(resp);
  }

}
