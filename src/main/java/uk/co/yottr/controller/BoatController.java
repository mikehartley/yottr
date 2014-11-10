package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.model.Boat;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ReferenceDataService;

import javax.validation.Valid;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Controller
public class BoatController {

	private static final Logger LOG = LoggerFactory.getLogger(BoatController.class);

    private BoatService boatService;
    private ReferenceDataService referenceDataService;

    @Autowired
    public BoatController(BoatService boatService, ReferenceDataService referenceDataService) {
        this.boatService = boatService;
        this.referenceDataService = referenceDataService;
    }

    @RequestMapping(value = "/s/listings/new", method = RequestMethod.GET)
	public String newListingPage(Model model) {
		LOG.info("Returning newListing.jsp page from newListingPage");
		model.addAttribute("boat", new Boat());
        model.addAttribute("ryaSailCruisingLevels", referenceDataService.ryaSailCruisingLevels());
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

    @RequestMapping(value = { "/", "index" }, method = RequestMethod.GET)
    public String index() {
        LOG.info("index page");
        return "index";
    }
}
