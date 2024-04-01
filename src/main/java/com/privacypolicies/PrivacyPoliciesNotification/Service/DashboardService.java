package com.privacypolicies.PrivacyPoliciesNotification.Service;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    public List<PrivacyOfWeb> loginDashboard(){
        return dashboardRepository.userWebsites();
    }

}
