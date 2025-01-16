package com.github.zyypj.tadeuBooter.minecraft.serialization;


import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.util.Base64;

class Base64Util {
    private Base64Util() {
    }

    public static String encode(byte[] buf) {
        return Base64.getEncoder().encodeToString(buf);
    }

    public static byte[] decode(String src) {
        try {
            return Base64.getDecoder().decode(src);
        } catch (IllegalArgumentException var4) {
            try {
                return Base64Coder.decodeLines(src);
            } catch (Exception var3) {
                throw var4;
            }
        }
    }
}