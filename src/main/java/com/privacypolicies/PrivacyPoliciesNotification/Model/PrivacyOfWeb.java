package com.privacypolicies.PrivacyPoliciesNotification.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

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

    @Column(name = "updated_policy")
    private String updatedPolicy;
}
