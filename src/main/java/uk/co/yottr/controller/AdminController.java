package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.form.MemberDetailsForm;
import uk.co.yottr.form.MemberSearch;
import uk.co.yottr.model.User;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;

@Controller
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private static final String REDIRECT_TO_MANAGE_USERS = "redirect:/admin/users";

    @Autowired
    private UserService userService;

    @Autowired
    private ReferenceDataService referenceDataService;

    /**
     * The members, by username, narrowed by the search. htmx asks for this as the search changes, and gets back
     * only the results.
     */
	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
	public ModelAndView getUsers(@ModelAttribute("search") MemberSearch search, @SortDefault(sort = "username") Pageable pageable,
                                 HttpServletRequest request, HttpServletResponse response) {
        final boolean resultsOnly = Htmx.wantsPartOfPage(request, response);
        final ModelAndView modelAndView = new ModelAndView(resultsOnly ? "manageUsers :: results" : "manageUsers");

        final Page<User> userPages = userService.search(search, pageable);
        modelAndView.addObject("wrapper", new PageWrapper<>(userPages));
        modelAndView.addObject("searchQuery", search.toQueryString());

        return modelAndView;
	}

    // Changes are POSTs, which need the CSRF token, and redirect afterwards so that a refresh doesn't repeat them.

    @RequestMapping(value = "/admin/user/{id}/delete", method = RequestMethod.POST)
    public String deleteUser(@PathVariable long id, Principal principal) {

        LOG.info("deleting user with ID {}", id);

        userService.delete(otherUser(id, principal));

        return REDIRECT_TO_MANAGE_USERS;
    }

    @RequestMapping(value = "/admin/user/{id}/enabled/flip", method = RequestMethod.POST)
    public String updateUserEnabledStatus(@PathVariable long id, Principal principal) {

        LOG.info("updating enabled status for user with ID {}", id);

        userService.flipEnabled(otherUser(id, principal));

        return REDIRECT_TO_MANAGE_USERS;
    }

    /**
     * An administrator may correct any member's details, their own included.
     */
    @RequestMapping(value = "/admin/user/{id}/edit", method = RequestMethod.GET)
    public ModelAndView editUserPage(@PathVariable long id) {
        final User user = member(id);
        return modelAndViewForMemberDetails(MemberDetailsForm.from(user), user);
    }

    @RequestMapping(value = "/admin/user/{id}/edit", method = RequestMethod.POST)
    public ModelAndView editUserAction(@PathVariable long id, @Valid @ModelAttribute("details") MemberDetailsForm details,
                                       BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        final User user = member(id);

        if (bindingResult.hasErrors()) {
            LOG.info("Errors editing the details of {}: {}", user.getUsername(), bindingResult.getAllErrors());
            return modelAndViewForMemberDetails(details, user);
        }

        details.applyTo(user);
        userService.save(user, false);

        LOG.info("Updated the details of {}", user.getUsername());

        redirectAttributes.addFlashAttribute("updated", user.getUsername());
        return new ModelAndView(REDIRECT_TO_MANAGE_USERS);
    }

    private ModelAndView modelAndViewForMemberDetails(MemberDetailsForm details, User user) {
        final ModelAndView modelAndView = new ModelAndView("memberDetails");
        modelAndView.addObject("details", details);
        modelAndView.addObject("member", user);
        modelAndView.addObject("countries", referenceDataService.countries());
        return modelAndView;
    }

    /**
     * The member with this ID, with their listings loaded.
     */
    private User member(long id) {
        return userService.findMemberById(id).orElseThrow(() -> {
            LOG.warn("User does not exist with ID {}", id);
            return new ResourceNotFoundException("User does not exist with ID " + id);
        });
    }

    /**
     * The user with this ID, who must not be the administrator making the change: an administrator who disabled
     * or deleted their own account could leave the site with no administrator at all.
     */
    private User otherUser(long id, Principal principal) {
        final User user = userService.findById(id).orElseThrow(() -> {
            LOG.warn("User does not exist with ID {}", id);
            return new ResourceNotFoundException("User does not exist with ID " + id);
        });

        if (user.getUsername().equals(principal.getName())) {
            throw new IllegalOperationException("Administrators can't disable or delete their own account.");
        }

        return user;
    }
}
