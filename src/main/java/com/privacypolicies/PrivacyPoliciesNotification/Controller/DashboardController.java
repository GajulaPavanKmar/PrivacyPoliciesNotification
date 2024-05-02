package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.PrivacyOfWeb;
import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.UserRepository;
import com.privacypolicies.PrivacyPoliciesNotification.Service.DashboardService;
import com.privacypolicies.PrivacyPoliciesNotification.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping(value = "/dashboard")
    public String dashboardPage(Model model,HttpSession session){
        User sessionUser = (User) session.getAttribute("user");
        if(sessionUser == null){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User User = userRepository.findByUserEmail(userName);
            if(User!=null){
                sessionUser = User;
                // Store user in session
                session.setAttribute("user", sessionUser);
                if (authentication != null && authentication.isAuthenticated()) {
                    User user = (User) authentication.getPrincipal();
                }
                System.out.println(sessionUser.toString());
            }else{
                System.out.println("User not found with email: " + userName);
                return "redirect:/login";
            }
        }
        Long userId = sessionUser.getUserId();
        List<PrivacyOfWeb> userWebsites = dashboardService.loginDashboard(userId);
        model.addAttribute("listOfValues",userWebsites);
        model.addAttribute("user",sessionUser);

        return "dashboard";
    }

    @GetMapping(value = "/profile")
    public String profilePage(Model model,HttpSession session){
        User sessionUser = (User) session.getAttribute("user");
        if(sessionUser == null){
            return "redirect:/login";
        }else{
            model.addAttribute("user", sessionUser);
            return "LoggedInUserPages/profilePage";
        }
    }

    @PostMapping(value = "/updateProfile")
    public String updateProfile(@ModelAttribute("user") User updatedUser, HttpSession session, RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            // User is not logged in or session expired
            return "redirect:/login";
        }

        // Call the service layer to update the user in the database
        User savedUser = userService.updateUserDetails(updatedUser);

        // Update the user object in the session
        if (savedUser != null) {
            session.setAttribute("user", savedUser);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile.");
        }

        return "redirect:/dashboard";
    }

    @GetMapping(value = "/addNewWebsites")
    public String addNewWebsites(){
        return "addNewWebsites.html";
    }

}
