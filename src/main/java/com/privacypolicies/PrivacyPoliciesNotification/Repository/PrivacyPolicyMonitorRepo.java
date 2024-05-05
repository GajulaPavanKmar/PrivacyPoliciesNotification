package com.privacypolicies.PrivacyPoliciesNotification.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Repository
public class PrivacyPolicyMonitorRepo {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PrivacyPolicyMonitorRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int updatePolicies(String storedPolicy, String currentPolicy, int websiteId){
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("America/New_York"));
        LocalDateTime now = zdt.toLocalDateTime();
        String sql ="Update privacyofweb set previous_policy =?, current_Policy =?, last_checked = ? where website_id =?";
        return jdbcTemplate.update(sql, storedPolicy, currentPolicy, now, websiteId);
    }
}
