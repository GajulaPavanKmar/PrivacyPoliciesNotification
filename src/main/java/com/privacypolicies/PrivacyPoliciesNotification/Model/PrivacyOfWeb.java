package com.privacypolicies.PrivacyPoliciesNotification.Model;

import lombok.Data;

@Data
public class PrivacyOfWeb {

    private String websiteName;
    private String websiteUrl;
    private String previousPolicy;
    private String updatedPolicy;
}
