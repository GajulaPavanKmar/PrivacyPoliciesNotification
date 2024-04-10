package com.privacypolicies.PrivacyPoliciesNotification.Configuration;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtilConfig {
    public static String generateSha256Hash(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return Hex.encodeHexString(encodedhash);
    }
}
