package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.WebScrapingRepo;
import com.privacypolicies.PrivacyPoliciesNotification.Service.EmailService;
import com.privacypolicies.PrivacyPoliciesNotification.Service.WebScrappingService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.privacypolicies.PrivacyPoliciesNotification.Service.ChatGptService;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
public class WebScrapingController {


    private static final Logger log = LoggerFactory.getLogger(WebScrapingController.class);
    @Autowired
    private WebScrappingService webScrappingService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ChatGptService chatGptService;

    @Autowired
    private WebScrapingRepo webScrapingRepo;

    @PostMapping(value = "/scrapAndSave")
    public String scrapAndSave(@ModelAttribute("privacyOfWeb") PrivacyOfWeb privacyOfWeb,
                               HttpSession session,
                               Model model) throws IOException {
        User user = (User) session.getAttribute("user");
        privacyOfWeb.setUser(user);
        String privacy = webScrappingService.scrapePrivacyPolicy(privacyOfWeb, privacyOfWeb.getWebsiteUrl(), true);
        if(! (privacy.equals("") || privacy.equals(null))){
            String emailBody = String.format(
                    "Hello, %s " +
                            "\n\nYou have added new website into your list Please find the details below." +
                            "\n\nWebsite Name: %s\nWebsite URL: %s\n\n" +
                            "Summary: %s"+
                            "\n\n\n Thank & Regards" +
                            "\n Notification Team",
                    privacyOfWeb.getUser().getFirstName(), privacyOfWeb.getWebsiteName(), privacyOfWeb.getWebsiteUrl(),privacyOfWeb.getCurrentPolicySummary()
            );


            model.addAttribute("content",privacy);
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
