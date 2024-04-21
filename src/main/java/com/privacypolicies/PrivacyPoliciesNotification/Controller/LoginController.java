package com.privacypolicies.PrivacyPoliciesNotification.Controller;

import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {


    private UserService userService;

    @Autowired
    public LoginController(UserService userService){
        this.userService = userService;
    }


    @GetMapping (value = "/login")
    public String loginPage(){
        return "loginPage";
    }


    @GetMapping(value = "/signUp")
    public String signUp(){
        return "signUpPage";
    }

    @PostMapping(value = "/signUp")
    public String createAccount(@ModelAttribute("user") User user,  RedirectAttributes redirectAttributes){
        User createAccount = userService.createNewAccount(user);
        if(createAccount != null){
            redirectAttributes.addFlashAttribute("msg","Your account created");
        }else{
            redirectAttributes.addFlashAttribute("msg","Unable to account created");
        }
        return "redirect:/login";
    }

}
