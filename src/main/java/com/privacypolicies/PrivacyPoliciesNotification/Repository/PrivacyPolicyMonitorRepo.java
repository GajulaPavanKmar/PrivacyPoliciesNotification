package com.privacypolicies.PrivacyPoliciesNotification.Repository;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrivacyPolicyMonitorRepo extends JpaRepository<PrivacyOfWeb, Integer> { // Ensure the ID type matches your entity ID type

    @Modifying
    @Transactional
    @Query("UPDATE PrivacyOfWeb p SET p.previousPolicy = :newPolicy, p.updatedPolicy = :currentPolicy WHERE p.websiteId = :websiteId")
    int updatePolicies(String newPolicy, String currentPolicy, int websiteId); // Changed parameter type to match entity ID type
}