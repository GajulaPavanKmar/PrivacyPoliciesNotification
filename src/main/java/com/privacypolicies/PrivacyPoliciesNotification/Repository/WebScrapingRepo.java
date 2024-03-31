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

    public int saveWebPolicy(String htmlContent){
        String sql = "INSERT INTO PrivacyOfWeb VALUES (?,?,?,?)";
        var rowMapper = BeanPropertyRowMapper.newInstance(PrivacyOfWeb.class);
        return jdbcTemplate.update(sql,rowMapper);
    }
    public List<PrivacyOfWeb> thePreviousOne(){
        String sql = "SELECT * FROM PrivacyOfWeb";
        var rowMapper = BeanPropertyRowMapper.newInstance(PrivacyOfWeb.class);
        List<PrivacyOfWeb> values =  jdbcTemplate.query(sql,rowMapper);
        return values;
    }
}
