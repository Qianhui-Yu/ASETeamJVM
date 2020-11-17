package com.jvm.coms4156.columbia.wehealth.controller;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.exception.BadAuthException;
import com.jvm.coms4156.columbia.wehealth.exception.PermissionException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {
    AuthenticatedUser au() throws BadAuthException {
        AuthenticatedUser au = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication();
        if (au == null) {
            throw new BadAuthException();
        }
        return au;
    }

    AuthenticatedUser admin() throws BadAuthException, PermissionException {
        AuthenticatedUser au = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication();
        if (au == null) {
            throw new BadAuthException();
        }
        if (au.getUserType() != DBUser.ADMIN) {
            throw new PermissionException("Here is admin access only");
        }
        return au;
    }
}