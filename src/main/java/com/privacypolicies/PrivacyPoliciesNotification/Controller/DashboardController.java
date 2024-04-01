package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;


    @GetMapping(value = "/dashboard")
    public String dashboardPage(@ModelAttribute("user") User user, Model model){
        List<PrivacyOfWeb> userWebsites = dashboardService.loginDashboard();
        model.addAttribute("listOfValues",userWebsites);
        return "dashboard";
    }

    @GetMapping(value = "/addNewWebsites")
    public String addNewWebsites(){
        return "addNewWebsites.html";
    }
}
