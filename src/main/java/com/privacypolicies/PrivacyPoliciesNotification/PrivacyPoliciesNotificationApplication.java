package com.privacypolicies.PrivacyPoliciesNotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrivacyPoliciesNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrivacyPoliciesNotificationApplication.class, args);
	}

}
