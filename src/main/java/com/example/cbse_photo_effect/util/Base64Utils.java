package com.example.cbse_photo_effect.util;

import java.util.Base64;

public class Base64Utils {

    public static String encodeBase64(byte[] data) {
        return Base64.getUrlEncoder().encodeToString(data);
    }

    public static byte[] decodeBase64(String base64String) {
        return Base64.getUrlDecoder().decode(base64String);
    }
}
