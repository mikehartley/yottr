package uk.co.yottr.controller;

import uk.co.yottr.model.Boat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Controller
public class BoatController {

	private static final Logger LOG = LoggerFactory.getLogger(BoatController.class);

	private Collection<Boat> boats = null;

	public BoatController(){
		boats = new ArrayList<Boat>();
        boats.addAll(dummyData());
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
		boats.add(boat);
		return "newListingSuccess";
	}

    @RequestMapping(value = "/s/listings/all", method = RequestMethod.GET)
    public ModelAndView listBoats() {
        LOG.info("All listings page");

        ModelAndView model = new ModelAndView("boatList");
        model.addObject("boats", boats);

        return model;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        LOG.info("index page");
        return "index";
    }

    @RequestMapping(value="/login")
    public String login(HttpServletRequest request, Model model) {
        LOG.info("Login page");
        return "login";
    }

    @RequestMapping(value="/logout")
    public String logout() {
        LOG.info("Logging out...");
        return "logout";
    }

    @RequestMapping(value="/denied")
    public String denied() {
        LOG.info("Access denied!");
        return "denied";
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
