package com.privacypolicies.PrivacyPoliciesNotification.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @RequestMapping(value = "/login")
    public String loginPage(){
        return "loginPage.html";
    }

    @RequestMapping(value = "/dashboard")
    public String loginDashboard(){
        return "dashboard.html";
    }

    @PostMapping(value="/notificationPre")
    public String notificationPre(Model model){
        return "redirect:/dashboard.html";
    }
}
