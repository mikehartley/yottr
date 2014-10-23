package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.model.User;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.UserService;

import javax.validation.Valid;
import java.security.Principal;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Controller
public class UserController {

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signup() {

        ModelAndView modelAndView = new ModelAndView("signup");
        modelAndView.addObject("user", new User());

        return modelAndView;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupAction(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            LOG.info(bindingResult.toString());
            return "signup";
        }

        model.addAttribute("user", user);
        user.addRole(Role.FREE);
        user.setEnabled(true);

        userService.save(user, true);

        LOG.info("Signed up new user: " + user);

        return "signupSuccess";
    }

    @RequestMapping(value = "/s/myDetails", method = RequestMethod.GET)
    public ModelAndView myDetails(Principal principal) {

        final String currentUsername = principal.getName();
        LOG.info("current username is: " + currentUsername);

        final User currentUser = userService.findByUsername(currentUsername);
        if (currentUser == null) {
            return new ModelAndView("index");
        }

        currentUser.setPassword(null);

        ModelAndView modelAndView = new ModelAndView("myDetails");
        modelAndView.addObject("user", currentUser);

        return modelAndView;
    }
}
