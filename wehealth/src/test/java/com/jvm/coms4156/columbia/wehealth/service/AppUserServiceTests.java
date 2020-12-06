package com.jvm.coms4156.columbia.wehealth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.github.javafaker.Faker;
import com.jvm.coms4156.columbia.wehealth.dao.AppUserDao;
import com.jvm.coms4156.columbia.wehealth.domain.AppUserInfo;
import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.domain.LoginRequest;
import com.jvm.coms4156.columbia.wehealth.domain.LoginResponse;
import com.jvm.coms4156.columbia.wehealth.domain.UserInput;
import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import com.jvm.coms4156.columbia.wehealth.exception.DuplicateException;
import com.jvm.coms4156.columbia.wehealth.exception.MissingDataException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Created by Ethan 11/15/2020.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppUserServiceTests {
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

    assertEquals(0, testUserInfo.getUserType());
    assertEquals(name, testUserInfo.getUsername());

    assertNotNull(testUserInfo.getLookupToken());
    LoginResponse resp = appUserService.verifyUser(testUserInfo.getLookupToken());
    System.out.println("response token: " + resp.getToken());

    assertNotNull(resp.getToken());
    AuthenticatedUser au = jwtService.verify(resp.getToken());


    String username = appUserDao.findByUserId(au.getUserId()).getUsername();
    assertEquals(name, username);
    assertEquals(0, au.getUserType());

  }

  @Test(expected = DuplicateException.class)
  public void registerTestExistingUser() throws Exception {
    appUserService.register(new UserInput("testUsername", "1", "1"));
    appUserService.register(new UserInput("testUsername", "1", "1"));
  }

  @Test(expected = MissingDataException.class)
  public void registerTestMissingUsername() throws Exception {
    appUserService.register(new UserInput("", "1", "1"));
  }

  @Test(expected = MissingDataException.class)
  public void registerTestMissingPassword() throws Exception {
    Faker faker = new Faker();
    String name = faker.name().fullName();
    appUserService.register(new UserInput(name, "", ""));
  }

  @Test
  public void loginTest() throws Exception {
    LoginResponse resp = appUserService.login(new LoginRequest("Test1", "123456"));
    assertNotNull(resp);
  }

  @Test
  public void loginTestUserNotFoundTest() throws Exception {
    LoginResponse resp = appUserService.login(new LoginRequest("UserName NotFound", "1"));
    assertNull(resp);
  }

  @Test
  public void loginTestLookUpTokenNotFoundTest() throws Exception {
    LoginResponse resp = appUserService.login(new LoginRequest("testuser", "1"));
    assertNull(resp);
  }

  @Test
  public void loginTestWithWrongPassword() throws Exception {
    LoginResponse resp = appUserService.login(new LoginRequest("Test1", "1"));
    assertNull(resp);
  }

  @Test(expected = NotFoundException.class)
  public void getUserInfoWithWrongId() throws Exception {
    AuthenticatedUser au = new AuthenticatedUser(100000L, 0, "Test");
    appUserService.getAppUserInfo(au);
  }

  @Test
  public void getUserInfoValidTest() throws Exception {
    AuthenticatedUser au = new AuthenticatedUser(2L, 0, "Test1");
    AppUserInfo userInfo = appUserService.getAppUserInfo(au);
    assertEquals(userInfo.getUsername(), "Test1");
    assertEquals(userInfo.getUserId(), 2L);
    assertEquals(userInfo.getUserType(), 0);
  }

  @Test(expected = NotFoundException.class)
  public void verifyUserLookUpTokenNotFoundTest() throws Exception {
    appUserService.verifyUser("InvalidLoopUpToken");
  }

  //  @Test
  //  public void deleteUserValidTest() throws DuplicateException {
  //    String name = "Delete Test";
  //    AppUserInfo userInfo = appUserService.register(
  //        new UserInput(name, "123456", "123456")
  //    );
  //    appUserService.deleteUser(new DbUser(name, userInfo.getLookupToken()));
  //    assertNull(appUserDao.findByUsername(name));
  //  }

}
