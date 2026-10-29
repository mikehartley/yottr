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
import uk.co.yottr.form.CrewProfileForm;
import uk.co.yottr.form.MyDetailsForm;
import uk.co.yottr.form.SignupForm;
import uk.co.yottr.model.CrewProfile;
import uk.co.yottr.model.User;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.CrewProfileService;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;

import jakarta.validation.Valid;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private UserService userService;
    private ReferenceDataService referenceDataService;
    private CrewProfileService crewProfileService;

    @Autowired
    public UserController(UserService userService, ReferenceDataService referenceDataService, CrewProfileService crewProfileService) {
        this.userService = userService;
        this.referenceDataService = referenceDataService;
        this.crewProfileService = crewProfileService;
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

        userService.signUp(user);

        LOG.info("Signed up new user {}", user.getUsername());

        return "signupSuccess";
    }

    @RequestMapping(value = "/s/myDetails*", method = RequestMethod.GET)
    public ModelAndView myDetails(Principal principal, Model model) {

        final String currentUsername = principal.getName();
        LOG.info("current username is: {}", currentUsername);

        final User currentUser = userService.findMember(currentUsername);

        return modelAndViewForMyDetails(MyDetailsForm.from(currentUser), currentUser, model);
    }

    @RequestMapping(value = "/s/myDetails", method = RequestMethod.POST)
    public ModelAndView myDetailsAction(@Valid @ModelAttribute("user") MyDetailsForm myDetailsForm, BindingResult bindingResult,
                                        Principal principal) {

        final User userForUpdate = userService.findMember(principal.getName());

        if (bindingResult.hasErrors()) {
            LOG.info("Errors from myDetails: {}", bindingResult.getAllErrors());
            return modelAndViewForMyDetails(myDetailsForm, userForUpdate, null);
        }

        myDetailsForm.applyTo(userForUpdate);

        if (myDetailsForm.isChangingPassword()) {
            userForUpdate.setPassword(myDetailsForm.getPassword());
            userService.save(userForUpdate, true);
        }
        else {
            userService.save(userForUpdate, false);
        }

        LOG.info("Updated the details of {}", userForUpdate.getUsername());

        return new ModelAndView("redirect:myDetails?updated");
    }

    /**
     * My Details, with the member's crew profile below if they're on Yottr as crew. A crew profile that wasn't
     * saved comes back from CrewProfileController as the flash attribute "profile", with its errors, and is shown
     * in place of the stored one.
     */
    private ModelAndView modelAndViewForMyDetails(MyDetailsForm myDetailsForm, User user, Model model) {
        ModelAndView modelAndView = new ModelAndView("myDetails");
        modelAndView.addObject("user", myDetailsForm);
        modelAndView.addObject("username", user.getUsername());
        modelAndView.addObject("listingCount", user.getBoatListings().size());
        modelAndView.addObject("maxListings", user.getMaxListings());
        modelAndView.addObject("countries", referenceDataService.countries());
        modelAndView.addObject("crewSide", user.isCrew());
        if (user.isCrew()) {
            final Optional<CrewProfile> profile = crewProfileService.profileOf(user.getUsername());
            if (model == null || !model.containsAttribute("profile")) {
                modelAndView.addObject("profile", profile.map(CrewProfileForm::from).orElseGet(CrewProfileForm::new));
            }
            modelAndView.addObject("profileExists", profile.isPresent());
            modelAndView.addObject("sailingRegions", referenceDataService.sailingRegions());
            modelAndView.addObject("frequencies", referenceDataService.frequencies());
            modelAndView.addObject("ryaSailCruisingLevels", referenceDataService.ryaSailCruisingLevels());
        }
        return modelAndView;
    }

    /**
     * Deletes the member's account and listings, then signs them out here: with CSRF protection on, /logout
     * only takes a POST, so it can't be reached by a redirect.
     */
    @RequestMapping(value = "/s/deleteMe", method = RequestMethod.POST)
    public String deleteMe(Principal principal, HttpServletRequest request) throws ServletException {

        final String currentUsername = principal.getName();
        LOG.info("deleting the account of {}", currentUsername);

        userService.findByUsername(currentUsername).ifPresent(userService::delete);

        request.logout();
        return "redirect:/index?logout";
    }
}
