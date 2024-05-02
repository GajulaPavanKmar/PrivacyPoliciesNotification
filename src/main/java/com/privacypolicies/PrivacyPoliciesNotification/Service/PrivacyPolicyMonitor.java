package com.privacypolicies.PrivacyPoliciesNotification.Service;


import com.privacypolicies.PrivacyPoliciesNotification.Configuration.HashUtilConfig;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.PrivacyPolicyMonitorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class PrivacyPolicyMonitor {

    @Autowired
    private PrivacyPolicyMonitorRepo privacyPolicyMonitorRepo;

    public String checkForUpdates(String storedPolicy, String currentPolicy, int websiteId) {
        try {
            String currentHash = HashUtilConfig.generateSha256Hash(storedPolicy);
            String lastHash = HashUtilConfig.generateSha256Hash(currentPolicy); // Retrieve the last hash from where you stored it
            if (!currentHash.equals(lastHash)) {
                int updatePolicy = privacyPolicyMonitorRepo.updatePolicies(storedPolicy, currentPolicy, websiteId);
                return "Privacy policy has changed.";


            } else {
                return null;
            }
        } catch (NoSuchAlgorithmException e) {
            return "Error while checking for updates.";
        }
    }
}
