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
import uk.co.yottr.model.Boat;
import uk.co.yottr.service.BoatService;

import javax.validation.Valid;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Controller
public class BoatController {

	private static final Logger LOG = LoggerFactory.getLogger(BoatController.class);

    private BoatService boatService;

    @Autowired
    public BoatController(BoatService boatService) {
        this.boatService = boatService;
    }

    @RequestMapping(value = "/s/listings/new", method = RequestMethod.GET)
	public String newListingPage(Model model) {
		LOG.info("Returning newListing.jsp page from newListingPage");
		model.addAttribute("boat", new Boat());
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
    public ModelAndView listBoats() {
        LOG.info("All listings page");

        ModelAndView modelAndView = new ModelAndView("boatList");

        modelAndView.addObject("boats", boatService.findAll());

        return modelAndView;
    }

    @RequestMapping(value = { "/", "/index**" }, method = RequestMethod.GET)
    public String index() {
        LOG.info("index page");
        return "index";
    }

    @RequestMapping(value = { "/test" }, method = RequestMethod.GET)
    public String test() {
        LOG.info("test page");
        return "test";
    }
}
