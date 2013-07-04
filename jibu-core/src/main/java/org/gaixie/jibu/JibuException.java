package org.gaixie.jibu;


/**
 * A base exception class for Jibu.
 */
public class JibuException extends Exception {
    public JibuException() {
        super();
    }


    /**
     * Construct JibuException with message string.
     *
     * @param s Error message string.
     */
    public JibuException(String s) {
        super(s);
    }


    /**
     * Construct JibuException, wrapping existing throwable.
     *
     * @param s Error message
     * @param t Existing connection to wrap.
     */
    public JibuException(String s, Throwable t) {
        super(s, t);
    }


    /**
     * Construct JibuException, wrapping existing throwable.
     *
     * @param t Existing exception to be wrapped.
     */
    public JibuException(Throwable t) {
        super(t);
    }
}
