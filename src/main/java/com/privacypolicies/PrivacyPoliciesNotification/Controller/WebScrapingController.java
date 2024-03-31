package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Service.WebScrappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class WebScrapingController {

    @Autowired
    private WebScrappingService webScrappingService;


    @PostMapping(value = "/scrapAndSave")
    public String scrapAndSave(@ModelAttribute("privacyOfWeb") PrivacyOfWeb privacyOfWeb, Model model) throws IOException {
        String privacy = webScrappingService.fetchPrivacyPolicy("https://www.youtube.com/");
        model.addAttribute("content",privacy);
        return "redirect:/webScrap";
    }
    @RequestMapping(value = "/webScrap")
    public String comparePrivacy(Model model){
        String value = webScrappingService.anyDiff();
        model.addAttribute("value",value);
        return "differencePage";
    }
}
