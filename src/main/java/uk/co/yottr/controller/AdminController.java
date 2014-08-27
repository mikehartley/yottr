package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.User;
import uk.co.yottr.tempDatastore.Database;

import java.util.Collection;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Controller
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private final Database database = new Database();

	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
	public ModelAndView getUsers() {

        LOG.info("landed in [get] users method");

        ModelAndView modelAndView = new ModelAndView("manageUsers");
        Collection<User> allUsers = database.getUsers().values();
        modelAndView.addObject("users", allUsers);

        LOG.info("Have put users into ModelAndView: " + allUsers);

		return modelAndView;
	}

    @RequestMapping(value = "/admin/user/{id}/delete", method = RequestMethod.GET)
    public ModelAndView deleteUser(@PathVariable long id) throws ResourceNotFoundException {

        LOG.info("deleting user with ID " + id);

        checkUserExists(id);

        database.getUsers().remove(id);

        ModelAndView modelAndView = new ModelAndView("manageUsers");
        Collection<User> allUsers = database.getUsers().values();
        modelAndView.addObject("users", allUsers);

        LOG.info("Have put users into ModelAndView: " + allUsers);

        return modelAndView;
    }

    @RequestMapping(value = "/admin/user/{id}/enabled/{enabled}", method = RequestMethod.GET)
    public ModelAndView updateUserEnabledStatus(@PathVariable long id, @PathVariable boolean enabled) throws ResourceNotFoundException {

        LOG.info("updating enabled status for user with ID " + id);

        checkUserExists(id);

        database.getUsers().remove(id);

        ModelAndView modelAndView = new ModelAndView("manageUsers");
        Collection<User> allUsers = database.getUsers().values();
        modelAndView.addObject("users", allUsers);

        LOG.info("Have put users into ModelAndView: " + allUsers);

        return modelAndView;
    }

    private void checkUserExists(long id) throws ResourceNotFoundException {
        if (!database.getUsers().containsKey(id)) {
            LOG.warn("User does not exist with ID " + id);
            throw new ResourceNotFoundException("User does not exist with ID " + id);
        }
    }
}
