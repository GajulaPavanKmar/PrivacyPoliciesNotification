package com.privacypolicies.PrivacyPoliciesNotification.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PrivacyPolicyMonitorRepo {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PrivacyPolicyMonitorRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int updatePolicies(String storedPolicy, String currentPolicy, int websiteId){
        String sql ="Update PrivacyOfWeb set previous_policy =?, current_Policy =? where website_id =?";
        return jdbcTemplate.update(sql, storedPolicy, currentPolicy, websiteId);
    }
}
