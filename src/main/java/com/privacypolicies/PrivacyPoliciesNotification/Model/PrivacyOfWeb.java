package com.privacypolicies.PrivacyPoliciesNotification.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PrivacyOfWeb")
public class PrivacyOfWeb {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "website_id")
    private int websiteId;

    @ManyToOne
    @JoinColumn(name = "user_ID", referencedColumnName = "user_id")
    private User user;

    @Column(name = "website_name")
    private String websiteName;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "previous_policy")
    private String previousPolicy;

    @Column(name = "current_policy")
    private String currentPolicy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;

    @Column(name = "current_policy_summary")
    private String currentPolicySummary;

    @Column(name = "what_is_the_difference")
    private String whatIsTheDifference;

    @Column(name = "website_privacy_link")
    private String websitePrivacyLink;
}
