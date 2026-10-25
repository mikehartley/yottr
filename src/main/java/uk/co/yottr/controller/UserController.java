package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.form.MyDetailsForm;
import uk.co.yottr.form.SignupForm;
import uk.co.yottr.model.User;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;

import jakarta.validation.Valid;
import java.security.Principal;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Controller
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private UserService userService;
    private ReferenceDataService referenceDataService;

    @Autowired
    public UserController(UserService userService, ReferenceDataService referenceDataService) {
        this.userService = userService;
        this.referenceDataService = referenceDataService;
    }

    // The forms bind to SignupForm and MyDetailsForm, never to User, so a request can't set the id, roles,
    // listing limit or enabled flag. Binding sign-up to User let a request with an id overwrite that account.

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signup() {

        ModelAndView modelAndView = new ModelAndView("signup");
        modelAndView.addObject("user", new SignupForm());
        modelAndView.addObject("countries", referenceDataService.countries());

        return modelAndView;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupAction(@Valid @ModelAttribute("user") SignupForm signupForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            LOG.info(bindingResult.toString());
            model.addAttribute("countries", referenceDataService.countries());
            return "signup";
        }

        final User user = signupForm.toUser();
        user.addRole(Role.FREE);
        user.setEnabled(true);

        userService.save(user, true);

        LOG.info("Signed up new user: " + user);

        return "signupSuccess";
    }

    @RequestMapping(value = "/s/myDetails*", method = RequestMethod.GET)
    public ModelAndView myDetails(Principal principal) {

        final String currentUsername = principal.getName();
        LOG.info("current username is: " + currentUsername);

        final User currentUser = userService.findByUsername(currentUsername);
        if (currentUser == null) {
            return new ModelAndView("index");
        }

        return modelAndViewForMyDetails(MyDetailsForm.from(currentUser), currentUser);
    }

    @RequestMapping(value = "/s/myDetails", method = RequestMethod.POST)
    public ModelAndView myDetailsAction(@Valid @ModelAttribute("user") MyDetailsForm myDetailsForm, BindingResult bindingResult,
                                        Principal principal) {

        final User userForUpdate = userService.findByUsername(principal.getName());

        if (bindingResult.hasErrors()) {
            LOG.info("Errors from myDetails: " + bindingResult.getAllErrors().toString());
            return modelAndViewForMyDetails(myDetailsForm, userForUpdate);
        }

        myDetailsForm.applyTo(userForUpdate);

        if (myDetailsForm.isChangingPassword()) {
            userForUpdate.setPassword(myDetailsForm.getPassword());
            userService.save(userForUpdate, true);
        }
        else {
            userService.save(userForUpdate, false);
        }

        LOG.info("Updated user: " + userForUpdate);

        return new ModelAndView("redirect:myDetails?updated");
    }

    private ModelAndView modelAndViewForMyDetails(MyDetailsForm myDetailsForm, User user) {
        ModelAndView modelAndView = new ModelAndView("myDetails");
        modelAndView.addObject("user", myDetailsForm);
        modelAndView.addObject("username", user.getUsername());
        modelAndView.addObject("listingCount", user.getBoatListings().size());
        modelAndView.addObject("maxListings", user.getMaxListings());
        modelAndView.addObject("countries", referenceDataService.countries());
        return modelAndView;
    }

    @RequestMapping(value = "/s/deleteMe", method = RequestMethod.GET)
    public String deleteMe(Principal principal) {

        final String logoutUrl = "redirect:/logout";

        final String currentUsername = principal.getName();
        LOG.info("current username is: " + currentUsername);

        final User currentUser = userService.findByUsername(currentUsername);
        if (currentUser == null) {
            return logoutUrl;
        }

        userService.delete(currentUser);
        return logoutUrl;
    }
}

