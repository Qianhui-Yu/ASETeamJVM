package com.jvm.coms4156.columbia.wehealth.controller;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.SUCCESS_MSG;

import com.jvm.coms4156.columbia.wehealth.domain.AppUserInfo;
import com.jvm.coms4156.columbia.wehealth.domain.LoginRequest;
import com.jvm.coms4156.columbia.wehealth.domain.LoginResponse;
import com.jvm.coms4156.columbia.wehealth.domain.UserInput;
import com.jvm.coms4156.columbia.wehealth.exception.BadAuthException;
import com.jvm.coms4156.columbia.wehealth.exception.DuplicateException;
import com.jvm.coms4156.columbia.wehealth.exception.MissingDataException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.service.AppUserService;
import com.sun.istack.NotNull;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by Ethan on 11/10/2020.
 */
@CrossOrigin
@RestController()
@RequestMapping(value = "/api/appUser")
public class UserController extends BaseController {

  private final AppUserService appUserService;

  @Autowired
  public UserController(AppUserService appUserService) {

    this.appUserService = appUserService;
  }

  /**
   * Post user login.
   *
   * @param request LoginRequest
   * @param res Response
   * @return LoginResponse
   * @throws BadAuthException Authentication Exception
   */
  @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
  public LoginResponse login(@RequestBody LoginRequest request, HttpServletResponse res) throws
          BadAuthException {
    LoginResponse out = appUserService.login(request);
    if (out == null) {
      throw new BadAuthException();
    }
    res.addCookie(buildCookie(out.getToken()));
    return out;
  }

  /**
   * Post user register.
   *
   * @param in Input
   * @return AppUserInfo
   * @throws DuplicateException Duplication user
   * @throws MissingDataException Mission one of the field
   */
  @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
  public AppUserInfo register(@RequestBody UserInput in)
          throws DuplicateException, MissingDataException {
    System.out.println(in.getUsername());
    return appUserService.register(in);
  }

  /**
   * Verify current jwt token.
   *
   * @param in Input
   * @param resp Response
   * @return Response
   * @throws NotFoundException Not found user base on jwt
   */
  @PostMapping(value = "/verify", consumes = "application/json", produces = "application/json")
  public LoginResponse verify(@RequestBody String in, HttpServletResponse resp) throws
      NotFoundException {
    LoginResponse out = appUserService.verifyUser(in);
    resp.addCookie(buildCookie(out.getToken()));
    return out;
  }

  /**
   * Build cookie base on token.
   *
   * @param token String
   * @return Cookie
   */
  @NotNull
  private Cookie buildCookie(String token) {
    Cookie cookie = new Cookie("authToken", token);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    return cookie;
  }


}