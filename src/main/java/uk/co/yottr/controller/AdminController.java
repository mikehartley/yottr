package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.User;
import uk.co.yottr.service.UserService;

import java.security.Principal;

@Controller
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private static final String REDIRECT_TO_MANAGE_USERS = "redirect:/admin/users";

    @Autowired
    private UserService userService;

	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
	public ModelAndView getUsers(Pageable pageable) {

        LOG.info("landed in [get] users method");

        return modelAndViewForManageUsers(pageable);
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

    private ModelAndView modelAndViewForManageUsers(Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("manageUsers");
        Page<User> userPages = userService.findAll(pageable);
        modelAndView.addObject("wrapper", new PageWrapper<>(userPages));
        return modelAndView;
    }
}
