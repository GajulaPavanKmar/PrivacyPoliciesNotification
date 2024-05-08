package com.privacypolicies.PrivacyPoliciesNotification.Service;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.UserRepository;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.WebScrapingRepo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class AutomaticService {

    private final WebDriver webDriver;
    private final UserRepository userRepository;
    private final WebScrapingRepo webScrapingRepo;
    private final WebScrappingService webScrappingService;
    private final EmailService emailService;

    @Autowired
    public AutomaticService(WebDriver webDriver, UserRepository userRepository,
                            WebScrapingRepo webScrapingRepo, WebScrappingService webScrappingService,
                            EmailService emailService) {
        this.webDriver = webDriver;
        this.userRepository = userRepository;
        this.webScrapingRepo = webScrapingRepo;
        this.webScrappingService = webScrappingService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 4 * * *", zone = "America/New_York")
    public void updateAllPrivacyPolicies() {
        List<User> allUsers = userRepository.findAll();
        allUsers.forEach(this::updatePrivacyPolicyForUser);
    }

    private void updatePrivacyPolicyForUser(User user) {
        user.getPrivacyPolicies().forEach(privacyOfWeb -> updatePolicyForWebsite(privacyOfWeb, user));
    }

    private void updatePolicyForWebsite(PrivacyOfWeb privacyOfWeb, User user) {
        String url = privacyOfWeb.getWebsiteUrl();
        String privacy = webScrappingService.scrapePrivacyPolicy(privacyOfWeb, url, true);
        if (privacy != null && !privacy.isEmpty()) {
            String emailBody = String.format(
                    "Hello, %s\n\nA change was detected in the privacy policy of the following website that you are monitoring:" +
                            "\nWebsite Name: %s\nWebsite URL: %s\n\n" +
                            "Thank & Regards,\nNotification Team",
                    user.getFirstName(), privacyOfWeb.getWebsiteName(), privacyOfWeb.getWebsiteUrl()
            );
            try {
                emailService.sendSimpleMessage(user.getUserEmail(), "Privacy Policy Update Notification", emailBody);
            } catch (Exception e) {
                // Log the exception or handle it appropriately
                log.error("Failed to send email to {}: {}", user.getUserEmail(), e.getMessage());
            }
        }
    }
}
