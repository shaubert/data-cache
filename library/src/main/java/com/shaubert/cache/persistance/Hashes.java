package com.shaubert.cache.persistance;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Hashes {

    public static String getSHA1(String text) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("SHA1");
            byte[] textBytes = text.getBytes();
            byte[] result = md5.digest(textBytes);
            return bytesToHex(result);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
