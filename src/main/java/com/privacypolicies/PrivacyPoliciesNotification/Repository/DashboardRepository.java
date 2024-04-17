package com.privacypolicies.PrivacyPoliciesNotification.Repository;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public String getThePolicy(Long websiteId) {
        String sql = "SELECT COALESCE(previous_policy, updated_policy) AS policy FROM PrivacyOfWeb WHERE website_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{websiteId}, String.class);
        } catch (EmptyResultDataAccessException e) {
            String msg = "No policy found for website ID: " + websiteId;
            return msg;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error accessing data", e);
        }
    }

    public PrivacyOfWeb specificWebisteDetails(Long websiteId) {
        String sql = "SELECT * FROM PrivacyOfWeb where website_id = ?";
        var rowMapper = BeanPropertyRowMapper.newInstance(PrivacyOfWeb.class);
        return jdbcTemplate.queryForObject(sql, new Object[]{websiteId}, rowMapper);
    }
}
