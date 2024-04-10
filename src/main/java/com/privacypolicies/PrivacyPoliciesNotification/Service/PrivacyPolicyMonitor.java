package com.privacypolicies.PrivacyPoliciesNotification.Service;

import com.privacypolicies.PrivacyPoliciesNotification.Configuration.HashUtilConfig;

import java.security.NoSuchAlgorithmException;

public class PrivacyPolicyMonitor {
    public static void checkForUpdates(String currentPolicyText) {
        try {
            String currentHash = HashUtilConfig.generateSha256Hash(currentPolicyText);
            // Assume `lastHash` is retrieved from your storage (e.g., file, database)
            String lastHash = "..."; // Retrieve the last hash from where you stored it

            if (!currentHash.equals(lastHash)) {
                System.out.println("Privacy policy has changed.");
                // Proceed to store `currentHash` for next time's comparison
                // and handle the change (e.g., notify users, analyze changes)
            } else {
                System.out.println("No changes detected in the privacy policy.");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
