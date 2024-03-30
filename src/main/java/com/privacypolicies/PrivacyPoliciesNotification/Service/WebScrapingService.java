package com.privacypolicies.PrivacyPoliciesNotification.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebScrapingService {
    private static final String[] PRIVACY_KEYWORDS = {"privacy", "privacy policy"};

    public String fetchPrivacyPolicy(String baseUrl) throws IOException {
        Document homePage = Jsoup.connect(baseUrl).get();

        Element privacyLinkElement = findPrivacyLink(homePage);

        if (privacyLinkElement != null) {
            String privacyUrl = privacyLinkElement.absUrl("href");
            Document privacyPolicyPage = Jsoup.connect(privacyUrl).userAgent("\"Mozilla/5.0\"").get();
            return privacyPolicyPage.text();
        }

        return "Privacy policy not found";
    }

    private Element findPrivacyLink(Document document) {
        Elements links = document.select("a[href]");
        for (Element link : links) {
            for (String keyword : PRIVACY_KEYWORDS) {
                if (link.text().toLowerCase().contains(keyword.toLowerCase())) {
                    return link;
                }
            }
        }
        return null;
    }
}