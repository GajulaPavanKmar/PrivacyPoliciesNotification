package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Service.EmailService;
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

    @Autowired
    private EmailService emailService;
    @PostMapping(value = "/scrapAndSave")
    public String scrapAndSave(@ModelAttribute("privacyOfWeb") PrivacyOfWeb privacyOfWeb,
                               User user,
                               Model model) throws IOException {
        String privacy = webScrappingService.fetchPrivacyPolicy(privacyOfWeb, privacyOfWeb.getWebsiteUrl());
        String emailBody = String.format(
                        "Hello, Pavan " +
                        "\n\nYou have addes new website into your list Please find the details below." +
                        "\n\nWebsite Name: %s\nWebsite URL: %s\n\nChange in privacy policies detected." +
                                "\n\n\n Thank & Regards" +
                                "\n Notification Team",
                privacyOfWeb.getWebsiteName(), privacyOfWeb.getWebsiteUrl()
        );
//        String emailAddress = user.getUserEmail();
        model.addAttribute("content",privacy);
        try {
            emailService.sendSimpleMessage(
                    "pavangajula1998@gmail.com",
                    "New website was added to your list",
                    emailBody
            );
            model.addAttribute("emailStatus", "Email sent successfully");
        } catch (Exception e) {
            model.addAttribute("emailStatus", "Failed to send email");
            // Log the exception or handle it accordingly
        }
        return "redirect:/dashboard";
    }

//    This method is for the testing
    @RequestMapping(value = "/showPolicies")
    public String showTheContent(Model model){
        String policy = webScrappingService.showPolicy();
        model.addAttribute("policyContent",policy);
        return "showPage";
    }
    @RequestMapping(value = "/webScrap")
    public String comparePrivacy(Model model){
        String value = webScrappingService.anyDiff();
        model.addAttribute("value",value);
        return "differencePage";
    }
}
