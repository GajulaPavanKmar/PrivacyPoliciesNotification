package com.privacypolicies.PrivacyPoliciesNotification.Repository;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class WebsiteRepo {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public WebsiteRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean deleteWebsite(int websiteId) {
        String sql = "DELETE FROM PrivacyOfWeb WHERE website_id=?";
        try{
            int rowsAffected = jdbcTemplate.update(sql, websiteId);
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        }catch (DataAccessException e) {
            return false;
        }

    }

    public PrivacyOfWeb valuesForEdit(int websiteId) {
        String sql = "SELECT * FROM PrivacyOfWeb WHERE website_id=?";
        var RowMapper = BeanPropertyRowMapper.newInstance(PrivacyOfWeb.class);
        return jdbcTemplate.queryForObject(sql, RowMapper, websiteId);
    }

    public int updateWebsite(PrivacyOfWeb privacyOfWeb) {
        String sql = "Update PrivacyOfWeb set website_name=?, website_Url =? where website_id=?";
        return jdbcTemplate.update(sql,privacyOfWeb.getWebsiteName(),
                privacyOfWeb.getWebsiteUrl(),privacyOfWeb.getWebsiteId());
    }
}
