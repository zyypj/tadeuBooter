package me.zyypj.booter.shared.codec;

import java.util.Base64;

class Base64Codec {

    public static String encode(byte[] buf) {
        return Base64.getEncoder().encodeToString(buf);
    }

    public static byte[] decode(String src) {
        try {
            return Base64.getDecoder().decode(src);
        } catch (Exception e) {
            throw new RuntimeException("Could not decode base64 string: " + src, e);
        }
    }
}
