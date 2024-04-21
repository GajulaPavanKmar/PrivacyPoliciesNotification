package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Service.EmailService;
import com.privacypolicies.PrivacyPoliciesNotification.Service.WebScrappingService;
import jakarta.servlet.http.HttpSession;
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
                               HttpSession session,
                               Model model) throws IOException {
        User user = (User) session.getAttribute("user");
        privacyOfWeb.setUser(user);
        String privacy = webScrappingService.scrapePrivacyPolicy(privacyOfWeb, privacyOfWeb.getWebsiteUrl(), true);
        if(!privacy.equals("") ){
            String emailBody = String.format(
                    "Hello, Pavan " +
                            "\n\nYou have added new website into your list Please find the details below." +
                            "\n\nWebsite Name: %s\nWebsite URL: %s\n\n" +
                            "\n\n\n Thank & Regards" +
                            "\n Notification Team",
                    privacyOfWeb.getWebsiteName(), privacyOfWeb.getWebsiteUrl()
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
