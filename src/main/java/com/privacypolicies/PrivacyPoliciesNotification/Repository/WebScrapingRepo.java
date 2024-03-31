package com.privacypolicies.PrivacyPoliciesNotification.Repository;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WebScrapingRepo {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public WebScrapingRepo(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public int saveWebPolicy(PrivacyOfWeb privacyOfWeb, String htmlContent){
        String sql = "INSERT INTO PrivacyOfWeb (websiteName, websiteUrl, previousPolicy, updatedPolicy) VALUES (?,?,?,?)";
        int count = jdbcTemplate.update(sql,privacyOfWeb.getWebsiteName(),
                privacyOfWeb.getWebsiteUrl(), htmlContent, "");
        return count;
    }
    public List<PrivacyOfWeb> thePreviousOne(){
        String sql = "SELECT * FROM PrivacyOfWeb";
        var rowMapper = BeanPropertyRowMapper.newInstance(PrivacyOfWeb.class);
        List<PrivacyOfWeb> values =  jdbcTemplate.query(sql,rowMapper);
        return values;
    }

    public String getPolicy() {
        String sql  = "SELECT previousPolicy FROM PrivacyOfWeb LIMIT 2";
        var rowMapper = BeanPropertyRowMapper.newInstance(PrivacyOfWeb.class);
        List<PrivacyOfWeb> previousPolicy = jdbcTemplate.query(sql,rowMapper);
        String policy = previousPolicy.toString();
        return policy;
    }
}
