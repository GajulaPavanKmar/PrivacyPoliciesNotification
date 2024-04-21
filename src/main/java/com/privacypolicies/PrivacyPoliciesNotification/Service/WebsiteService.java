package com.privacypolicies.PrivacyPoliciesNotification.Service;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.WebsiteRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WebsiteService {


    @Autowired
    private WebsiteRepo websiteRepo;

    /*public List<PrivacyOfWeb> findAllWebsite(){
        return websiteRepo.findAll();
    }

    public PrivacyOfWeb findWebsiteById(int id){
        return websiteRepo.findById(id).orElse(null);
    }

    public PrivacyOfWeb saveWebsite(PrivacyOfWeb privacyOfWeb) {
        return websiteRepo.save(privacyOfWeb);
    }*/

    public String deleteWebsite(int id) {
        log.info("Attempting to delete website with ID: {}", id);
        boolean isDeleted =   websiteRepo.deleteWebsite(id);
        if (isDeleted) {
            return "Success";
        } else {
            log.warn("No website found with ID: {}", id);
            return null;
        }
    }


    public PrivacyOfWeb editWebsite(int id) {
        log.info("Attempting to edit website with ID: {}", id);
        PrivacyOfWeb editValues = websiteRepo.valuesForEdit(id);
        if (editValues == null) {
            log.warn("No website found with ID: {}", id);
            return null;
        }
        return editValues;
    }

    public String updateWebsite(PrivacyOfWeb privacyOfWeb) {
        log.info("Attempting to update the website with ID: {} ",privacyOfWeb.getWebsiteId());
        int updateValues = websiteRepo.updateWebsite(privacyOfWeb);
        if (updateValues <=0 ) {
            log.warn("No website found with ID: {}", privacyOfWeb.getWebsiteId());
            return null;
        }else{
            return "Success";
        }
    }
}
