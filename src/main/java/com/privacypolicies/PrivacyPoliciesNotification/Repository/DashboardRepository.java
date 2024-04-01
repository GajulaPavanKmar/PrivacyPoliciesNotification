package com.privacypolicies.PrivacyPoliciesNotification.Repository;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardRepository {


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DashboardRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PrivacyOfWeb> userWebsites(){
        String sql = "SELECT * FROM PrivacyOfWeb";
        var rowMapper = BeanPropertyRowMapper.newInstance(PrivacyOfWeb.class);
        List<PrivacyOfWeb> values =  jdbcTemplate.query(sql,rowMapper);
        return values;
    }
}
