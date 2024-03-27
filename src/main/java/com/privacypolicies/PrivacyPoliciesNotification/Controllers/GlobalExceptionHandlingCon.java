package com.privacypolicies.PrivacyPoliciesNotification.Controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandlingCon {

    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHandler(Exception exception){
        ModelAndView model = new ModelAndView();
        model.setViewName("error");
        model.addObject("errormsg", exception.getMessage());
        return model;
    }
}
