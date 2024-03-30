package com.privacypolicies.PrivacyPoliciesNotification.Controllers;

import com.privacypolicies.PrivacyPoliciesNotification.Service.WebScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class webScraping {

    @Autowired
    private WebScrapingService webScrapingService;
    @RequestMapping(value = "/webScrap")
    public String webScraping(Model model) throws IOException {
        String privacy = webScrapingService.fetchPrivacyPolicy("https://www.youtube.com/");
        model.addAttribute("content",privacy);
        return "home.html";
    }
}
