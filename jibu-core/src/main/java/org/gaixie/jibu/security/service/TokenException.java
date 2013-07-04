package org.gaixie.jibu.security.service;

import org.gaixie.jibu.JibuException;

/**
 * Token Exception class.
 */
public class TokenException extends JibuException {
    public TokenException() {
        super();
    }

    /**
     * Construct TokenException with message string.
     *
     * @param s Error message string.
     */
    public TokenException(String s) {
        super(s);
    }
}
