package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.UserRepository;
import com.privacypolicies.PrivacyPoliciesNotification.Service.*;
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

    @Autowired
    private EmailService emailService;

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
                if(!(change.equals("") || change.equals(null))) {
                    String emailBody = String.format(
                            "Hello, User " +
                                    "\n\nWe have detected a change in the privacy policy in " +
                                    "\n\nWebsite Name: %s\nWebsite URL: %s\n\nChange in privacy policies detected." +
                                    "\n\n\nThank & Regards" +
                                    "\nNotification Team",
                            userWebsite.getWebsiteName(), userWebsite.getWebsiteUrl()
                    );
                    String emailAddress = user.getUserEmail();
                    try {
                        emailService.sendSimpleMessage(
                                emailAddress,
                                "New website was added to your list",
                                emailBody
                        );
                        model.addAttribute("emailStatus", "Email sent successfully");
                    } catch (Exception e) {
                        model.addAttribute("emailStatus", "Failed to send email");
                    }
                }

            }
        }
        userWebsite = dashboardService.specificWebsite(websiteId);
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
