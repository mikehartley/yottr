package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.model.User;
import uk.co.yottr.tempDatastore.Database;

import java.util.Collection;

@Controller
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private final Database database = new Database();

	@RequestMapping(value = "/admin/manageUsers", method = RequestMethod.GET)
	public ModelAndView manageUsers() {

        LOG.info("landed in manageUsers method");

        ModelAndView modelAndView = new ModelAndView("manageUsers");
        Collection<User> allUsers = database.getUsers().values();
        modelAndView.addObject("users", allUsers);

        LOG.info("Have put users into ModelAndView: " + allUsers);

		return modelAndView;
	}
}
