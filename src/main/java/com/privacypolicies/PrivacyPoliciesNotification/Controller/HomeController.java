package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(value = {"/","","/home"})
    public String homePage(){
        return "homePage";
    }

    @GetMapping("/homeprivacypolicy")
    public String privacyPolicy() {
        return "privacypolicy";
    }
}
