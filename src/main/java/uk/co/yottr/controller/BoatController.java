package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;

import javax.validation.Valid;
import java.security.Principal;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Controller
public class BoatController {

	private static final Logger LOG = LoggerFactory.getLogger(BoatController.class);

    private BoatService boatService;
    private UserService userService;
    private ReferenceDataService referenceDataService;

    @Autowired
    public BoatController(BoatService boatService, UserService userService, ReferenceDataService referenceDataService) {
        this.boatService = boatService;
        this.userService = userService;
        this.referenceDataService = referenceDataService;
    }

    @RequestMapping(value = "/s/listings/new", method = RequestMethod.GET)
	public String newListingPage(Model model, Principal principal) {
		LOG.info("Returning newListing.jsp page from newListingPage");

        final User user = userService.findByUsername(principal.getName());

        model.addAttribute("boat", new Boat(user));
        model.addAttribute("ryaSailCruisingLevels", referenceDataService.ryaSailCruisingLevels());
        model.addAttribute("sailingStyles", referenceDataService.sailingStyles());
        model.addAttribute("hullTypes", referenceDataService.hullTypes());

        return "newListing";
	}

	@RequestMapping(value = "/s/listings/new", method = RequestMethod.POST)
	public String newListingAction(@Valid Boat boat, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
            LOG.info(bindingResult.toString());
			LOG.info("Returning newListing.jsp page from saveBoatAction");
			return "newListing";
		}

        LOG.info("Returning newListingSuccess.jsp page");

        model.addAttribute("boat", boat);
        boatService.save(boat);

        return "newListingSuccess";
	}

    @RequestMapping(value = "/s/listings/all", method = RequestMethod.GET)
    public ModelAndView listBoats(Pageable pageable) {
        LOG.info("All listings page");

        ModelAndView modelAndView = new ModelAndView("boatList");

        Page<Boat> boatPages = boatService.findAll(pageable);
        modelAndView.addObject("wrapper", new PageWrapper<>(boatPages));

        return modelAndView;
    }

    @RequestMapping(value = "/s/listings/mine", method = RequestMethod.GET)
    public ModelAndView myListings(Principal principal) {
        LOG.info("My listings page");

        final User user = userService.findByUsername(principal.getName());
        return modelAndViewForMyListings(user);
    }

    @RequestMapping(value = "/s/listings/{boatReference}/suspended/flip", method = RequestMethod.GET)
    public ModelAndView updateUserEnabledStatus(@PathVariable String boatReference, Principal principal) throws ResourceNotFoundException {

        LOG.info("updating suspended status for boat listing with reference " + boatReference);

        final Boat boat = boatService.findByReference(boatReference);
        if (boat == null) {
            throw new ResourceNotFoundException("No boat found with reference " + boatReference);
        }

        final User user = userService.findByUsername(principal.getName());
        checkThatBoatBelongsToPrincipal(boat, user);

        boat.setSuspended(!boat.isSuspended());
        boatService.save(boat);

        return modelAndViewForMyListings(user);
    }

    private void checkThatBoatBelongsToPrincipal(Boat boat, User user) throws ResourceNotFoundException {
        final String boatReference = boat.getReference();

        for (Boat userBoat : user.getBoatListings()) {
            if (userBoat.getReference().equals(boatReference)) {
                return;
            }
        }

        throw new ResourceNotFoundException(String.format("No boat with reference %s found for user %s", boatReference, user.getUsername()));
    }

    private ModelAndView modelAndViewForMyListings(User user) {
        final ModelAndView modelAndView = new ModelAndView("myListings");
        modelAndView.addObject("boatListings", user.getBoatListings());
        return modelAndView;
    }

    @RequestMapping(value = { "/", "index" }, method = RequestMethod.GET)
    public String index() {
        LOG.info("index page");
        return "index";
    }
}
