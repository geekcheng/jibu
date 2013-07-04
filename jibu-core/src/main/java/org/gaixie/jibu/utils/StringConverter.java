package org.gaixie.jibu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UTFDataFormatException;

/**
 * Collection of static methods for converting strings between different
 * formats and to and from byte arrays
 *
 *
 */
public class StringConverter {

    private static final char HEXCHAR[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
        'e', 'f'
    };
    private static final String HEXINDEX = "0123456789abcdef0123456789ABCDEF";

    /**
     * Compacts a hexadecimal string into a byte array
     *
     *
     * @param s hexadecimal string
     *
     * @return byte array for the hex string
     * @throws IOException
     */
    public static byte[] hexToByte(String s) throws IOException {

        int  l      = s.length() / 2;
        byte data[] = new byte[l];
        int  j      = 0;

        if (s.length() % 2 != 0) {
            throw new IOException(
                                  "hexadecimal string with odd number of characters");
        }

        for (int i = 0; i < l; i++) {
            char c = s.charAt(j++);
            int  n, b;

            n = HEXINDEX.indexOf(c);

            if (n == -1) {
                throw new IOException(
                                      "hexadecimal string contains non hex character");
            }

            b       = (n & 0xf) << 4;
            c       = s.charAt(j++);
            n       = HEXINDEX.indexOf(c);
            b       += (n & 0xf);
            data[i] = (byte) b;
        }

        return data;
    }

    /**
     * Converts a byte array into a hexadecimal string
     *
     *
     * @param b byte array
     *
     * @return hex string
     */
    public static String byteToHex(byte b[]) {

        int    len = b.length;
        char[] s   = new char[len * 2];

        for (int i = 0, j = 0; i < len; i++) {
            int c = ((int) b[i]) & 0xff;

            s[j++] = HEXCHAR[c >> 4 & 0xf];
            s[j++] = HEXCHAR[c & 0xf];
        }

        return new String(s);
    }

    public static String byteToString(byte[] b, String charset) {

        try {
            return (charset == null) ? new String(b)
                : new String(b, charset);
        } catch (Exception e) {}

        return null;
    }
}