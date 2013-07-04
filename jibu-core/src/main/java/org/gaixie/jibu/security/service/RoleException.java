package org.gaixie.jibu.security.service;

import org.gaixie.jibu.JibuException;

/**
 * Role Exception class.
 */
public class RoleException extends JibuException {
    public RoleException() {
        super();
    }

    /**
     * Construct RoleException with message string.
     *
     * @param s Error message string.
     */
    public RoleException(String s) {
        super(s);
    }
}
