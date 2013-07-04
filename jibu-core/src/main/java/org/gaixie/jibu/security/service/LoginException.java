package org.gaixie.jibu.security.service;

import org.gaixie.jibu.JibuException;

/**
 * Login Exception class.
 */
public class LoginException extends JibuException {
    public LoginException() {
        super();
    }

    /**
     * Construct LoginException with message string.
     *
     * @param s Error message string.
     */
    public LoginException(String s) {
        super(s);
    }
}
