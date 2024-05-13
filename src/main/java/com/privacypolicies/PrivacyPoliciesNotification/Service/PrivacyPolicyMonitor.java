package com.privacypolicies.PrivacyPoliciesNotification.Service;


import com.privacypolicies.PrivacyPoliciesNotification.Configuration.HashUtilConfig;
import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.PrivacyPolicyMonitorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Service
public class PrivacyPolicyMonitor {

    @Autowired
    private PrivacyPolicyMonitorRepo privacyPolicyMonitorRepo;

    @Autowired
    private ChatGptService chatGptService;

    public String checkForUpdates(String storedPolicy, String currentPolicy, PrivacyOfWeb privacyOfWeb) {
        try {
            int websiteId = privacyOfWeb.getWebsiteId();
            String currentHash = HashUtilConfig.generateSha256Hash(storedPolicy);
            String lastHash = HashUtilConfig.generateSha256Hash(currentPolicy); // Retrieve the last hash from where you stored it
            if (!currentHash.equals(lastHash)) {
                int updatePolicy = privacyPolicyMonitorRepo.updatePolicies(storedPolicy, currentPolicy, websiteId);
                    List<String> instructions = Arrays.asList(
                        "Please read the 2 texts throughly and tell me what changed from the first one.",
                        "You need give the information about the change in formal way.",
                        "The first text is stored policy in our database and the second one is current policy of the website",
                            "Just give me what changed like Previous Policy used to have collected X data now the website is collecting Y data. Like this. I want"
                );
                String comparisonResult = chatGptService.compareTexts(storedPolicy, currentPolicy, instructions);
                privacyOfWeb.setWhatIsTheDifference(comparisonResult);
                if (comparisonResult != null && !comparisonResult.isEmpty()) {
                    List<String> summarizeInstructions = Arrays.asList(
                            "I have extracted the text of a privacy policy from a website, but it includes redundant headers, navigation items, and other irrelevant information. Please clean up the text by removing any repeated headers, navigational elements, and leaving only the privacy policy",
                            "Here is the cleaned-up text of a privacy policy. Please provide a summary that highlights the key points relevant to an end-user. Focus on what information is collected, why it is collected, how it is used, the user's control over their data, and security measures. The summary should be easily understandable and useful for helping users make informed decisions about their data privacy."
                    );
                    String summarizedText = chatGptService.summarizeText(currentPolicy, summarizeInstructions);
                    int savechange = privacyPolicyMonitorRepo.compareAndSaveSummary(comparisonResult, summarizedText, websiteId);
                }
                return "Changed";


            } else {
                return "No change.";
            }
        } catch (NoSuchAlgorithmException e) {
            return "Error while checking for updates.";
        }
    }
}
