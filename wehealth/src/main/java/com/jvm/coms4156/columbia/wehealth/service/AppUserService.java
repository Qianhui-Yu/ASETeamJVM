package com.jvm.coms4156.columbia.wehealth.service;

import com.jvm.coms4156.columbia.wehealth.dao.AppUserDao;
import com.jvm.coms4156.columbia.wehealth.domain.AppUserInfo;
import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.domain.LoginRequest;
import com.jvm.coms4156.columbia.wehealth.domain.LoginResponse;
import com.jvm.coms4156.columbia.wehealth.domain.UserInput;
import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.entity.Field;
import com.jvm.coms4156.columbia.wehealth.exception.DuplicateException;
import com.jvm.coms4156.columbia.wehealth.exception.MissingDataException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

public class AppUserService {
  private final JwtService jwtService;
  private final AppUserDao appUserDao;
  @Autowired
  public AppUserService(AppUserDao appUserDao, JwtService jwtService){
    this.appUserDao = appUserDao;
    this.jwtService = jwtService;
  }

  @Transactional
  public LoginResponse login(LoginRequest in) {
    DBUser user = appUserDao.findByUsername(in.getUsername());
    if (user == null  || user.getLookup_token() == null ) {
      return null;
    }
    if (passwordNotmatch(user, in.getPassword())) {
      return null;
    }

    return logUserIn(user);
  }

  private LoginResponse logUserIn(DBUser user) {
    long exp = jwtService.getExpiration();
    String token = jwtService.generate(user.getUser_id(), user.getUser_type(), exp);

    return new LoginResponse(new AppUserInfo(user), token, exp);
  }

  public AppUserInfo getAppUserInfo(AuthenticatedUser authUser) throws NotFoundException {
    DBUser user = appUserDao.findByUserId(authUser.getUserId());
    if (user == null) {
      throw new NotFoundException("User not found with provided id");
    }
    return new AppUserInfo(user);
  }

  @Transactional
  public AppUserInfo register(UserInput in) throws DuplicateException, MissingDataException {
    if (StringUtils.isEmpty(in) || StringUtils.isEmpty(in.getUsername()) || StringUtils.isEmpty(in.getCurrentPassword()) ) {
      throw new MissingDataException("Missing username or password");
    }
    DBUser user = appUserDao.findByUsername(in.getUsername());
    if (user != null) {
      throw new DuplicateException("There is user with this username already exists");
    }

    String lookupToken = UUID.randomUUID().toString();
    user = new DBUser(in.getUsername(), "v:" + lookupToken);
    user.setPassword(in.getNewPassword());
    user.setUsername(in.getUsername());
    appUserDao.save(user);

    return new AppUserInfo(user);
  }



  private boolean passwordNotmatch(DBUser u, String clearPassword) {
    return !new BCryptPasswordEncoder().matches(u.getSalt() + "&&" + clearPassword, u.getPassword_hash());
  }
}
