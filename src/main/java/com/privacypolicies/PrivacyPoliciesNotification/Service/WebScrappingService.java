package com.privacypolicies.PrivacyPoliciesNotification.Service;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.WebScrapingRepo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebScrappingService {


    private final WebDriver webDriver;

    public WebScrappingService(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Autowired
    private WebScrapingRepo webScrapingRepo;

    private static final String[] PRIVACY_KEYWORDS = {"privacy", "privacy policy"};


    public String scrapePrivacyPolicy(PrivacyOfWeb privacyOfWeb, String url, boolean store) {
        String scrapedPolicy = scrapeWithJsoup(privacyOfWeb, url, store);
        if (scrapedPolicy == null) {
            scrapedPolicy = scrapeWithSelenium(privacyOfWeb, url, store);
        }
        return scrapedPolicy != null ? scrapedPolicy : "Privacy policy not found.";
    }

    private String scrapeWithJsoup(PrivacyOfWeb privacyOfWeb, String url, boolean store) {
        try {
            Document homepage = Jsoup.connect(url).get();
            Element privacyLinkElement = findPrivacyLink(homepage);
            if (privacyLinkElement != null) {
                String privacyUrl = privacyLinkElement.absUrl("href");
                Document privacyPolicyPage = Jsoup.connect(privacyUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(10 * 1000)
                        .get();
                String policyText = privacyPolicyPage.text();
                if (!policyText.isEmpty()) {
                    if(store){
                        webScrapingRepo.saveWebPolicy(privacyOfWeb, policyText);
                    }
                    return policyText;
                }
            }
        } catch (IOException e) {
            log.error("IOException occurred while fetching URL: {}", url, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
        }
        return null;
    }

    private String scrapeWithSelenium(PrivacyOfWeb privacyOfWeb, String url, boolean store) {
        webDriver.get(url);
        WebElement privacyPolicyLink = findPrivacyPolicyLink(webDriver);
        if (privacyPolicyLink != null) {
            privacyPolicyLink.click();
            new WebDriverWait(webDriver, Duration.ofSeconds(5))
                    .until(webDriver1 -> ((JavascriptExecutor) webDriver1)
                            .executeScript("return document.readyState")
                            .equals("complete"));
            String privacyPolicy = extractPrivacyPolicyText();
            if (!privacyPolicy.isEmpty()) {
                if(store){
                    webScrapingRepo.saveWebPolicy(privacyOfWeb, privacyPolicy);
                }
                return privacyPolicy;
            }
        }
        return null; // Indicate that Selenium also failed
    }


    private String extractPrivacyPolicyText() {
        String[] containerSelectors = {
                "article", "section", "div#privacy", "div.privacy-policy",
                "main", "div.content", "footer"
        };
        StringBuilder extractedText = new StringBuilder();

        for (String selector : containerSelectors) {
            try {
                List<WebElement> elements = webDriver.findElements(By.cssSelector(selector));
                for (WebElement element : elements) {
                    extractedText.append(element.getText()).append("\n\n");
                }
                if (!extractedText.toString().isEmpty()) {
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        if (extractedText.toString().isEmpty()) {
            extractedText.append(webDriver.findElement(By.tagName("body")).getText());
        }

        return extractedText.toString().trim();
    }


    private WebElement findPrivacyPolicyLink(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        List<String> linkTexts = Arrays.asList(
                "Privacy Policy", "Privacy Notice", "Privacy",
                "Privacy and Cookies", "Data Protection"
        );

        for (String linkText : linkTexts) {
            WebElement linkElement = tryFindElement(wait, By.linkText(linkText));
            if (linkElement != null) return linkElement;

            WebElement partialLinkElement = tryFindElement(wait, By.partialLinkText(linkText));
            if (partialLinkElement != null) return partialLinkElement;
        }
        return null;
    }

    private WebElement tryFindElement(WebDriverWait wait, By by) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            return null; // Return null if element is not found within the timeout
        }
    }

    private Element findPrivacyLink(Document document) {
        String[] cssSelectors = new String[]{
                "footer a[href]", // Privacy links are often in the footer
                "nav a[href]", // Or in the main navigation
                "a[href*='privacy']", // Links containing 'privacy' in the href attribute
                "a[href*='legal']", // Some sites categorize privacy under 'legal' sections
        };

        List<String> privacyKeywords = Arrays.asList("privacy policy", "privacy notice", "privacy", "data protection");

        for (String selector : cssSelectors) {
            for (String keyword : privacyKeywords) {
                Elements foundLinks = document.select(selector).stream()
                        .filter(link -> link.text().toLowerCase().contains(keyword.toLowerCase()))
                        .collect(Collectors.toCollection(Elements::new));
                if (!foundLinks.isEmpty()) {
                    return foundLinks.first();
                }
            }
        }
        Elements links = document.select("a[href]");
        for (Element link : links) {
            String hrefValue = link.attr("href").toLowerCase();
            for (String keyword : privacyKeywords) {
                if (link.text().toLowerCase().contains(keyword.toLowerCase()) || hrefValue.contains(keyword.replace(" ", "").toLowerCase())) {
                    return link;
                }
            }
        }
        return null;
    }



    public String anyDiff(){
        List<PrivacyOfWeb> listValues = webScrapingRepo.thePreviousOne();
        StringBuilder differences = new StringBuilder();

        for( PrivacyOfWeb listValue : listValues){
            String prevPolicy = listValue.getPreviousPolicy();
            String updatedPolicy = listValue.getUpdatedPolicy();

            List<String> original = Arrays.asList(prevPolicy.split("/n"));
            List<String> revised = Arrays.asList(updatedPolicy.split("/n"));
            Patch<String> patch = DiffUtils.diff(original, revised);
            if (!patch.getDeltas().isEmpty()) {
                differences.append("Differences found:\n");
                patch.getDeltas().forEach(delta -> differences.append(delta.toString()).append("\n"));
            }
        }
        if (differences.length() == 0) {
            return "No differences found.";
        } else {
            return differences.toString();
        }
    }

    public String showPolicy() {
        String policy = webScrapingRepo.getPolicy();
        return policy;
    }
}
