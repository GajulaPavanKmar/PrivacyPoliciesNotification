package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Service.WebsiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;


    @GetMapping("/editWebsite/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        PrivacyOfWeb privacyOfWeb = websiteService.editWebsite(id);
        if (privacyOfWeb == null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("website", privacyOfWeb);
        return "LoggedInUserPages/EditWebsite";
    }

    @PostMapping("/updateWebsite")
    public String updateWebsite(@ModelAttribute("website") PrivacyOfWeb privacyOfWeb, RedirectAttributes redirectAttributes) {
        String isUpdated = websiteService.updateWebsite(privacyOfWeb);
        if (isUpdated == null) {
            redirectAttributes.addAttribute("msg","Updation Failed");
        }
        redirectAttributes.addAttribute("msg","Website Updated");
        return "redirect:/dashboard";
    }

    @GetMapping("/deleteWebsite/{id}")
    public String deleteWebsite(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        log.info("Received request to delete website with ID: {}", id);
        String value = websiteService.deleteWebsite(id);
        if (value != null) {
            redirectAttributes.addFlashAttribute("msg", "Website deleted successfully");
            log.info("Deletion successful for website ID: {}", id);
        } else {
            redirectAttributes.addFlashAttribute("msg", "Unable to delete website");
            log.warn("Deletion failed for website ID: {}", id);
        }
        return "redirect:/dashboard";
    }


    /*@GetMapping("/addNewWebsites")
    public String addNewWebsite(Model model) {
        PrivacyOfWeb privacyOfWeb = new PrivacyOfWeb();
        model.addAttribute("website", privacyOfWeb);
        return "add_website";  // Name of the new website form view
    }*/
    /*@PostMapping("/saveWebsite")
    public String saveWebsite(@ModelAttribute("website") PrivacyOfWeb privacyOfWeb) {
        websiteService.saveWebsite(privacyOfWeb);
        return "redirect:/dashboard";
    }*/
}
