package com.jvm.coms4156.columbia.wehealth.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.exception.BadAuthExecption;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtServiceTest {


  @Autowired
  private JwtService jwtService;

  @Test
  public void getExpirationTest() throws Exception {
    long exp = jwtService.getExpiration();
    assertTrue((System.currentTimeMillis() + (30 * 60 * 1000))  < exp + 5000);
  }

  @Test
  public void generateTokenTest() throws Exception {
    Long id = 10L;
    int userType = 0;
    String token = jwtService.generate(id, userType, jwtService.getExpiration());
    AuthenticatedUser au = jwtService.verify(token);

    assertEquals(id.toString(), String.valueOf(au.getUserId()));
    assertEquals(userType, au.getUserType());
  }

  @Test(expected = BadAuthExecption.class)
  public void generateTokenExpireTest() throws Exception {
    String token = jwtService.generate(10L, 0, System.currentTimeMillis() - 5000);
    AuthenticatedUser au = jwtService.verify(token);
  }

}
