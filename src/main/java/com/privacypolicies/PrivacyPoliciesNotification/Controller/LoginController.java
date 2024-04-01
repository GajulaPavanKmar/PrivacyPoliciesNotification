package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {


    private UserService userService;

    @Autowired
    public LoginController(UserService userService){
        this.userService = userService;
    }


    @RequestMapping(value = "/login")
    public String loginPage(){
        return "loginPage";
    }


    @PostMapping("/signin")
    public String signIn(@ModelAttribute("user")User user, Model model) {
        User existingUser = userService.findByEmail(user.getUserEmail());
        if (existingUser != null && existingUser.getUserPassword().equals(user.getUserPassword())) {
            model.addAttribute("user", existingUser);
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Invalid email or password");
        return "signin";
    }
}
