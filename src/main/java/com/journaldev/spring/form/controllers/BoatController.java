package com.journaldev.spring.form.controllers;

import com.journaldev.spring.form.model.Boat;
import com.journaldev.spring.form.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;

@Controller
public class BoatController {

	private static final Logger logger = LoggerFactory.getLogger(BoatController.class);

	private Collection<Boat> boats = null;

	public BoatController(){
		boats = new ArrayList<Boat>();
        boats.addAll(dummyData());
	}

	@RequestMapping(value = "/listings/new", method = RequestMethod.GET)
	public String newListingPage(Model model) {
		logger.info("Returning newListing.jsp page from newListingPage");
		model.addAttribute("boat", new Boat());
		return "newListing";
	}

	@RequestMapping(value = "/listings/new", method = RequestMethod.POST)
	public String newListingAction(@Valid Boat boat, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
            logger.info(bindingResult.toString());
			logger.info("Returning newListing.jsp page from saveBoatAction");
			return "newListing";
		}
		logger.info("Returning newListingSuccess.jsp page");
		model.addAttribute("boat", boat);
		boats.add(boat);
		return "newListingSuccess";
	}

    @RequestMapping(value = "/listings/all", method = RequestMethod.GET)
    public ModelAndView listBoats() {
        logger.info("All listings page");

        ModelAndView model = new ModelAndView("boatList");
        model.addObject("boats", boats);

        return model;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        logger.info("START : index page");
        return "index";
    }

    private Collection<Boat> dummyData() {
        List<Boat> dummyList = new ArrayList<>();

        dummyList.add(createBoat("Y0001", "Halberg Rassy", "HR42", 42, Boat.HullType.MONO, "Looking for some crew to do some sailing."));
        dummyList.add(createBoat("Y0002", "Jaguar", "SeaCat", 55, Boat.HullType.MULTI, "I just like to motor about then have some lunch."));

        return dummyList;
    }

    private Boat createBoat(String reference, String manufacturer, String model, int length,
                            Boat.HullType hullType, String description) {
        Boat boat = new Boat();
        boat.setReference(reference);
        boat.setManufacturer(manufacturer);
        boat.setModel(model);
        boat.setLength(length);
        boat.setHullType(hullType);
        boat.setDesc(description);
        return boat;
    }


}
