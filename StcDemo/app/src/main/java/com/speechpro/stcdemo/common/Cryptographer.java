package com.speechpro.stcdemo.common;

import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Created by Alexander Grigal on 09.02.18.
 */

public class Cryptographer {

    private static final String CHARSET = "UTF-8";
    private static final String ALGORITHM = "SHA-1";

    public static String sha1Hex(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        byte[] textBytes = text.getBytes(CHARSET);
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public static byte[] sha1bin(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        byte[] textBytes = text.getBytes(CHARSET);
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return sha1hash;
    }

    @Nullable
    public static String sha1binBase64(String text) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] textBytes = new byte[0];
        try {
            textBytes = text.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return (Base64.encodeToString(sha1hash, Base64.DEFAULT)).trim();
    }

    public static boolean isBase64(String stringBase64){
        String regex =
                "([A-Za-z0-9+/]{4})*"+
                        "([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)";

        Pattern pattern = Pattern.compile(regex);

        if (!pattern.matcher(stringBase64).matches()) {
            return false;
        } else {
            return true;
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

}
