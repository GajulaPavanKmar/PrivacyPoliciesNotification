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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        List<PrivacyOfWeb> userWebsites = dashboardService.loginDashboard();
        model.addAttribute("listOfValues",userWebsites);
        model.addAttribute("user",sessionUser);

        return "dashboard";
    }

    @GetMapping(value = "/profile")
    public String profilePage(Model model,HttpSession session){
        User sessionUser = (User) session.getAttribute("user");
        if(sessionUser == null){
            model.addAttribute("user", sessionUser);
        }else{
            return "redirect:/login";
        }
        return "LoginPages/profilePage";
    }

    /*@PutMapping(value = "updateProfile")
    public String updateProfilePage(@ModelAttribute("user") User user, Model model,HttpSession session){
        User sessionUser = (User) session.getAttribute("user");
        try {
            String msg = userService.updateUser(sessionUser, user);
            return "redirect:/profile";
        }catch (Exception e){
            model.addAttribute("msg", e.getMessage());
            return "redirect:/profile";
        }
    }*/
    @GetMapping(value = "/addNewWebsites")
    public String addNewWebsites(){
        return "addNewWebsites.html";
    }
}
