package com.privacypolicies.PrivacyPoliciesNotification.Repository;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class WebScrapingRepo {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public WebScrapingRepo(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public int saveWebPolicy(PrivacyOfWeb privacyOfWeb, String htmlContent, String privacyUrl){
        if (privacyOfWeb.getUser() == null || privacyOfWeb.getUser().getUserId() == null) {
            throw new IllegalStateException("PrivacyOfWeb must have a non-null User with a non-null userId");
        }
        String sql = "INSERT INTO privacyofweb " +
                "(user_ID,website_Name,  website_Url, previous_Policy, " +
                "current_Policy,created_at,website_privacy_link, current_policy_summary) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        LocalDateTime now = LocalDateTime.now();

        int count = jdbcTemplate.update(sql,privacyOfWeb.getUser().getUserId(),
                privacyOfWeb.getWebsiteName(),privacyOfWeb.getWebsiteUrl(), "",
                htmlContent,now,privacyUrl, privacyOfWeb.getCurrentPolicySummary());
        return count;
    }
    public List<PrivacyOfWeb> thePreviousOne(){
        String sql = "SELECT * FROM privacyofweb";
        var rowMapper = BeanPropertyRowMapper.newInstance(PrivacyOfWeb.class);
        List<PrivacyOfWeb> values =  jdbcTemplate.query(sql,rowMapper);
        return values;
    }

    public String getPolicy() {
        String sql  = "SELECT previousPolicy FROM privacyofweb LIMIT 2";
        var rowMapper = BeanPropertyRowMapper.newInstance(PrivacyOfWeb.class);
        List<PrivacyOfWeb> previousPolicy = jdbcTemplate.query(sql,rowMapper);
        String policy = previousPolicy.toString();
        return policy;
    }

}
