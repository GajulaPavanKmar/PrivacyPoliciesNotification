package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/sendEmail")
    public String sendEmailNotification(){
        emailService.sendSimpleMessage(
                "pavangajula1998@gmail.com",
                "Change in privacy policies",
                "Hello, this is a test email from Spring Boot."
        );
        return "Email sent successfully";
    }
}
