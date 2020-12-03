package com.jvm.coms4156.columbia.wehealth.service;

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
import com.sun.istack.NotNull;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class AppUserService {
  private final JwtService jwtService;
  private final AppUserDao appUserDao;

  @Autowired
  public AppUserService(AppUserDao appUserDao, JwtService jwtService) {
    this.appUserDao = appUserDao;
    this.jwtService = jwtService;
  }

  /**
   * Log user in.
   *
   * @param in LoginRequest
   * @return LoginResponse
   */
  public LoginResponse login(LoginRequest in) {
    DbUser user = appUserDao.findByUsername(in.getUsername());
    if (user == null  || user.getLookupToken() == null) {
      return null;
    }
    if (passwordNotMatch(user, in.getPassword())) {
      return null;
    }

    return logUserIn(user);
  }

  /**
   * Log in user and refresh jwt.
   *
   * @param user DbUser
   * @return Response for login objcet
   */
  private LoginResponse logUserIn(DbUser user) {
    long exp = jwtService.getExpiration();
    String token = jwtService.generate(user.getUsername(), user.getUserId(),
            user.getUserType(), exp);

    return new LoginResponse(new AppUserInfo(user), token, exp);
  }

  /**
   * Get user info from base controller.
   *
   * @param authUser AuthenticatedUser
   * @return AppUserInfo
   * @throws NotFoundException Not Found
   */
  public AppUserInfo getAppUserInfo(AuthenticatedUser authUser) throws NotFoundException {
    DbUser user = appUserDao.findByUserId(authUser.getUserId());
    if (user == null) {
      throw new NotFoundException("User not found with provided id");
    }
    return new AppUserInfo(user);
  }

  /**
   * Register user with user input.
   *
   * @param in Input
   * @return AppUserInfo
   * @throws DuplicateException Duplicate exception
   * @throws MissingDataException Missing one of the field
   */
  @Transactional
  public AppUserInfo register(UserInput in) throws DuplicateException, MissingDataException {

    if (StringUtils.isEmpty(in.getUsername())
            || StringUtils.isEmpty(in.getCurrentPassword())) {
      throw new MissingDataException("Missing username or password");
    }
    DbUser user = appUserDao.findByUsername(in.getUsername());

    if (user != null) {
      throw new DuplicateException("There is user with this username already exists");
    }

    String lookupToken = UUID.randomUUID().toString();
    user = saveUser("v:" + lookupToken, in.getNewPassword(), in.getUsername(), 0);
    appUserDao.save(user);

    return new AppUserInfo(user);
  }

  /**
   * Create a new DbUser.
   *
   * @param lookupToken String
   * @param password String
   * @param username String
   * @param userType int
   * @return Created user
   */
  public DbUser saveUser(String lookupToken, String password, String username, int userType) {
    DbUser user = new DbUser(username, lookupToken);
    user.setPassword(password);
    user.setUsername(username);
    user.setUserType(userType);
    return user;
  }

  /**
   * Verify user base on lookup token.
   *
   *  @param lookupToken String
   * @return LoginResponse
   * @throws NotFoundException not found
   */
  @Transactional
  public LoginResponse verifyUser(String lookupToken) throws NotFoundException {
    DbUser user = appUserDao.findByLookupToken(lookupToken);
    if (user == null) {
      throw new NotFoundException("Unable to find user with this lookupToken");
    }
    user.setLookupToken(null);
    appUserDao.save(user);

    return logUserIn(user);
  }

  /**
   * Check whether password not match.
   *
   * @param u DbUser
   * @param clearPassword String
   * @return boolean
   */
  private boolean passwordNotMatch(DbUser u, String clearPassword) {
    return !new BCryptPasswordEncoder().matches(u.getSalt() + "&&"
            + clearPassword, u.getPasswordHash());
  }
}
