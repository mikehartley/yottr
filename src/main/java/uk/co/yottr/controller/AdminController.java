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
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.User;
import uk.co.yottr.service.UserService;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Controller
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
	public ModelAndView getUsers(Pageable pageable) {

        LOG.info("landed in [get] users method");

        return modelAndViewForManageUsers(pageable);
	}

    @RequestMapping(value = "/admin/user/{id}/delete", method = RequestMethod.GET)
    public ModelAndView deleteUser(@PathVariable long id, Pageable pageable) throws ResourceNotFoundException {

        LOG.info("deleting user with ID " + id);

        checkUserExists(id);

        userService.delete(id);

        return modelAndViewForManageUsers(pageable);
    }

    @RequestMapping(value = "/admin/user/{id}/enabled/flip", method = RequestMethod.GET)
    public ModelAndView updateUserEnabledStatus(@PathVariable long id, Pageable pageable) throws ResourceNotFoundException {

        LOG.info("updating enabled status for user with ID " + id);

        checkUserExists(id);

        User user = userService.findById(id);
        user.setEnabled(!user.isEnabled());
        userService.save(user, false);

        return modelAndViewForManageUsers(pageable);
    }

    private void checkUserExists(long id) throws ResourceNotFoundException {
        if (!userService.userExists(id)) {
            LOG.warn("User does not exist with ID " + id);
            throw new ResourceNotFoundException("User does not exist with ID " + id);
        }
    }

    private ModelAndView modelAndViewForManageUsers(Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("manageUsers");
        Page<User> userPages = userService.findAll(pageable);
        modelAndView.addObject("wrapper", new PageWrapper<>(userPages));
        return modelAndView;
    }
}
