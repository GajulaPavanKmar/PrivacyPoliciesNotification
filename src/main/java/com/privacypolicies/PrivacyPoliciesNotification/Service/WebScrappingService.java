package com.privacypolicies.PrivacyPoliciesNotification.Service;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.WebScrapingRepo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.DeltaType;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WebScrappingService {


    private final WebDriver webDriver;

    public WebScrappingService(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Autowired
    private WebScrapingRepo webScrapingRepo;

    @Autowired
    private ChatGptService chatGptService;


    public String scrapePrivacyPolicy(PrivacyOfWeb privacyOfWeb, String url, boolean store) {
        // Attempt to find the privacy policy link using the methods you provided
        String privacyUrl = findPrivacyLink(url);
        if (privacyUrl != null) {
            // Scrape the content from the found privacy policy URL
            privacyOfWeb.setWebsitePrivacyLink(privacyUrl);
            String privacy =  scrapePrivacyPolicyContent(privacyOfWeb, privacyUrl, store);

            return privacy;

        } else {
            log.error("No privacy policy link found for URL: {}", url);
            return null;
        }
    }

    private String findPrivacyLink(String url) {
        try {
            Connection.Response initialResponse = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                    .method(Connection.Method.GET)
                    .execute(); // First connect to get cookies

            Document homepage = initialResponse.parse(); // Parse homepage with received cookies
            Elements links = homepage.select("footer a:matchesOwn((?i)privacy|privacy policy|privacy notice|privacy statement|data protection|terms of use|legal notice)");
            if (links.isEmpty()) {
                // Define an ordered list of possible texts for privacy policies
                List<String> orderedPrivacyTexts = Arrays.asList(
                        "privacy", "privacy policy", "privacy notice", "privacy statement", "data protection", "terms of use", "legal notice"
                );

                // Check the footer for each of the ordered privacy texts
                for (String text : orderedPrivacyTexts) {
                    links = homepage.select(String.format(
                            "footer a:containsOwn(%s), div.footer a:containsOwn(%s)", text, text
                    ));
                    if (!links.isEmpty()) {
                        return links.first().absUrl("href");
                    }
                }

                // If no link found in the footer with the ordered privacy texts, check the entire document
                for (String text : orderedPrivacyTexts) {
                    links = homepage.select(String.format("a:containsOwn(%s)", text));
                    if (!links.isEmpty()) {
                        return links.first().absUrl("href");
                    }
                }

                // If no specific text matches, use the original broader search
                Elements broadMatchLinks = homepage.select("a[href~=(?i)\\b(privacy|policy|legal|terms|protection)\\b]");
                if (!broadMatchLinks.isEmpty()) {
                    return broadMatchLinks.first().absUrl("href");
                }
            }else {
                return links.first().absUrl("href");
            }

        } catch (HttpStatusException e) {
            log.error("HTTP error fetching URL. Status={}, URL={}", e.getStatusCode(), url, e);
            if (e.getStatusCode() == 403) {
                return findPrivacyLinkWithSelenium(url);
            }
        } catch (IOException e) {
            log.error("IOException occurred while fetching URL using Jsoup: {}", url, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching URL using Jsoup: {}", url, e);
        }
        // Fallback to Selenium
        return findPrivacyLinkWithSelenium(url);
    }


    private String findPrivacyLinkWithSelenium(String url) {
        WebDriver webDriver = setupWebDriver(); // Configure and return a WebDriver

        try {
            webDriver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); // Disable implicit waits
            webDriver.get(url);

            // Handle any potential alerts or pop-ups
            try {
                WebDriverWait alertWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
                alertWait.until(ExpectedConditions.alertIsPresent());
                webDriver.switchTo().alert().accept();
            } catch (NoAlertPresentException e) {
                // No alert was present, continue with execution
            } catch (TimeoutException e) {
                // Alert did not appear in time
                log.warn("No alert present within the time frame: {}", e.getMessage());
            }

            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20)); // Use a more generous timeout for finding elements
            List<String> possibleTexts = Arrays.asList(
                    "Privacy Policy", "Privacy Notice", "Privacy",
                    "Privacy and Cookies", "Data Protection", "Privacy Statement");

            for (String text : possibleTexts) {
                try {
                    WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(text)));
                    ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", link);
                    wait.until(ExpectedConditions.elementToBeClickable(link));  // Recheck if clickable after scrolling
                    if (link.isDisplayed()) {  // Additional check to confirm element is displayed
                        return link.getAttribute("href");
                    }
                } catch (TimeoutException ex) {
                    // Continue searching for other texts
                    log.warn("Timeout while searching for text '{}': {}", text, ex.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            log.error("Privacy policy link not found on the page using Selenium: {}", url, e);
        } catch (WebDriverException e) {
            log.error("WebDriver exception occurred using Selenium: {}", url, e);
            e.printStackTrace();  // Print stack trace for detailed debugging information
        } finally {
            webDriver.quit();  // Ensure the WebDriver is quit after execution to free up resources
        }
        return null;
    }

    private WebDriver setupWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Essential for running on a server without GUI
        options.addArguments("--no-sandbox"); // Security model workaround, may be necessary in certain environments
        options.addArguments("--disable-dev-shm-usage"); // Avoid issues in limited resource environments
        options.addArguments("--window-size=1920,1080"); // Specify a window size in headless mode

        // If you need to set a specific page load strategy
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL); // Adjust this as per your requirements

        return new ChromeDriver(options);
    }



    private String scrapePrivacyPolicyContent(PrivacyOfWeb privacyOfWeb, String privacyUrl, boolean store) {
        String policyText = null;
        // First attempt with Jsoup
        try {
            Document privacyPolicyPage = Jsoup.connect(privacyUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10 * 1000)
                    .get();
            policyText = extractTextUsingSelectors(privacyPolicyPage);
        } catch (IOException e) {
            log.error("Jsoup failed to fetch privacy policy content: {}", privacyUrl, e);
        }
        // Check if Jsoup successfully retrieved the content
        if (policyText == null || policyText.isEmpty()) {
            // Fallback to Selenium if Jsoup fails
            policyText = scrapeWithSelenium(privacyUrl);
        }
        // Check the content retrieved by Selenium
        if (policyText != null && !policyText.isEmpty()) {
            if (store) {
                List<String> instructions = Arrays.asList(
                        "I have extracted the text of a privacy policy from a website, but it includes redundant headers, navigation items, and other irrelevant information. Please clean up the text by removing any repeated headers, navigational elements, and leaving only the  privacy policy",
                        "Here is the cleaned-up text of a privacy policy. Please provide a summary that highlights the key points relevant to an end-user. Focus on what information is collected, why it is collected, how it is used, the user's control over their data, and security measures. The summary should be easily understandable and useful for helping users make informed decisions about their data privacy."
                );
                String summarizedText = chatGptService.summarizeText(policyText, instructions);
                privacyOfWeb.setCurrentPolicySummary(summarizedText);
                privacyOfWeb.setCurrentPolicy(policyText);
                webScrapingRepo.saveWebPolicy(privacyOfWeb, policyText, privacyUrl);
            }
            return policyText;
        } else {
            log.error("Both Jsoup and Selenium failed to retrieve privacy policy content from: {}", privacyUrl);
            return "Privacy policy text not found.";
        }
    }

    private String extractTextUsingSelectors(Document document) {
        // Define common selectors used in privacy policy pages
        String[] selectors = new String[]{"article", "section", "div#privacy", "div.policy", "main", "div.content"};
        for (String selector : selectors) {
            Elements elements = document.select(selector);
            if (!elements.isEmpty()) {
                return elements.text(); // Return text of the first matching element
            }
        }
        return document.body().text(); // Fallback to entire body text if no specific elements are found
    }

    private String scrapeWithSelenium(String url) {
        try {
            webDriver.get(url);
            // Wait until the page is completely loaded
            new WebDriverWait(webDriver, Duration.ofSeconds(10))
                    .until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            // This uses a broad approach to capture as much text as possible
            return webDriver.findElement(By.tagName("body")).getText();
        } catch (Exception e) {
            log.error("Selenium failed to fetch content from URL: {}", url, e);
            return null;
        }
    }


    public String anyDiff() {
        List<PrivacyOfWeb> listValues = webScrapingRepo.thePreviousOne();
        StringBuilder differences = new StringBuilder();

        for (PrivacyOfWeb listValue : listValues) {
            String prevPolicy = listValue.getPreviousPolicy();
            String updatedPolicy = listValue.getCurrentPolicy();

            List<String> original = Arrays.asList(prevPolicy.split("\n"));
            List<String> revised = Arrays.asList(updatedPolicy.split("\n"));

            Patch<String> patch;
            try {
                patch = DiffUtils.diff(original, revised);
                if (!patch.getDeltas().isEmpty()) {
                    differences.append("Differences found:\n");
                    for (AbstractDelta<String> delta : patch.getDeltas()) {
                        switch (delta.getType()) {
                            case CHANGE:
                                differences.append("Changed from: \n")
                                        .append(String.join("\n", delta.getSource().getLines()))
                                        .append("\nTo: \n")
                                        .append(String.join("\n", delta.getTarget().getLines()))
                                        .append("\n");
                                break;
                            case DELETE:
                                differences.append("Deleted: \n")
                                        .append(String.join("\n", delta.getSource().getLines()))
                                        .append("\n");
                                break;
                            case INSERT:
                                differences.append("Inserted: \n")
                                        .append(String.join("\n", delta.getTarget().getLines()))
                                        .append("\n");
                                break;
                        }
                        differences.append("\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
