package com.privacypolicies.PrivacyPoliciesNotification.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

    @Bean
    public WebDriver webDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        // Toggle headless mode based on an environment variable or configuration setting
        // Remove or comment out the following line to run Chrome with GUI
        options.addArguments("--headless");

        options.addArguments("--disable-gpu");

        // Additional options for better performance and compatibility
        options.addArguments("--window-size=1920,1080"); // Specify window size
        options.addArguments("--no-sandbox"); // Bypass OS security model, REQUIRED for Docker
        options.addArguments("--disable-dev-shm-usage"); // Overcome limited resource problems

        return new ChromeDriver(options);
    }
}
