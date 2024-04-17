package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.UserRepository;
import com.privacypolicies.PrivacyPoliciesNotification.Service.DashboardService;
import com.privacypolicies.PrivacyPoliciesNotification.Service.OpenNlpService;
import com.privacypolicies.PrivacyPoliciesNotification.Service.PrivacyPolicyMonitor;
import com.privacypolicies.PrivacyPoliciesNotification.Service.WebScrappingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class ChangeDetectionController {

    @Autowired
    private PrivacyPolicyMonitor privacyPolicyMonitor;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private WebScrappingService webScrappingService;

    @GetMapping("/changeDetection/{websiteId}")
    public String changeDetection(@PathVariable("websiteId") Long websiteId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        String storedPolicy = dashboardService.previousPolicy(websiteId);
        PrivacyOfWeb userWebsite = dashboardService.specificWebsite(websiteId);
        userWebsite.setUser(user);

        String currentPolicy = "";
        String change ="";
        if(userWebsite != null) {
            currentPolicy = webScrappingService.scrapePrivacyPolicy(userWebsite, userWebsite.getWebsiteUrl(), false);
            if(!(currentPolicy.equals(""))) {
                change = privacyPolicyMonitor.checkForUpdates(storedPolicy,currentPolicy, userWebsite.getWebsiteId());
            }
        }
        
        model.addAttribute("listOfValues",userWebsite);
        return "LoggedInUserPages/websiteDifference";
    }
//    @RequestMapping("/findDifference")
//    public String detectChange(@ModelAttribute("privacyOfWeb") PrivacyOfWeb privacyOfWeb, Model model){
//        List<PrivacyOfWeb> details = dashboardService.loginDashboard();
//        String change = "";
//        for (PrivacyOfWeb detail : details) {
////            change = privacyPolicyMonitor.checkForUpdates(detail.getPreviousPolicy(),detail.getUpdatedPolicy());
//        }
//        model.addAttribute("value",change);
//        return "dashboard";
//    }
}
