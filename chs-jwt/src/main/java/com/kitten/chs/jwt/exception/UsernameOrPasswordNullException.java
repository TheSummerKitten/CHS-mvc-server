package com.kitten.chs.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author kitten
 */
public class UsernameOrPasswordNullException extends AuthenticationException {

    public UsernameOrPasswordNullException(String msg) {
        super(msg);
    }

}
