package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.UserService;

import javax.validation.Valid;
import java.security.Principal;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
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
	public ModelAndView newListingPage(Principal principal) {
        final User user = userService.findByUsername(principal.getName());

        ModelAndView modelAndView = new ModelAndView("newListing");

        modelAndView.addObject("boat", new Boat(user));
        addModelAttributesForNewListing(modelAndView, user);

        return modelAndView;
	}

    @RequestMapping(value = "/s/listings/new", method = RequestMethod.POST)
	public ModelAndView newListingAction(@Valid Boat boat, BindingResult bindingResult, Principal principal) throws IllegalOperationException {
        final User user = userService.findByUsername(principal.getName());

        if (bindingResult.hasErrors()) {
            LOG.info(bindingResult.toString());
            ModelAndView modelAndView = new ModelAndView("newListing");
            addModelAttributesForNewListing(modelAndView, user);
            return modelAndView;
        }

        ModelAndView modelAndView = new ModelAndView("newListingSuccess");

        checkIsWithinListingLimit(user);

        boat.setOwner(user);
        boatService.save(boat);

        modelAndView.addObject("boat", boat);

        return modelAndView;
	}

    @RequestMapping(value = "/s/listings/all", method = RequestMethod.GET)
    public ModelAndView listBoats(Pageable pageable) {
        LOG.info("All listings page");

        ModelAndView modelAndView = new ModelAndView("boatList");

        Page<Boat> boatPages = boatService.findAllExcludingSuspended(pageable);
        modelAndView.addObject("wrapper", new PageWrapper<>(boatPages));

        return modelAndView;
    }

    @RequestMapping(value = "/s/listings/mine*", method = RequestMethod.GET)
    public ModelAndView myListings(Principal principal) {
        LOG.info("My listings page");

        return modelAndViewForMyListings(principal);
    }

    @RequestMapping(value = "/s/listings/{boatReference}/suspended/flip", method = RequestMethod.GET)
    public ModelAndView updateUserEnabledStatus(@PathVariable String boatReference, Principal principal) throws ResourceNotFoundException {

        LOG.info("updating suspended status for boat listing with reference " + boatReference);

        final Boat boat = findBoat(boatReference, principal);

        boat.setSuspended(!boat.isSuspended());
        boatService.save(boat);

        return modelAndViewForMyListings(principal);
    }

    @RequestMapping(value = "/s/listings/{boatReference}/delete", method = RequestMethod.GET)
    public ModelAndView deleteUser(@PathVariable String boatReference, Principal principal) throws ResourceNotFoundException {

        LOG.info("deleting listing with boat reference " + boatReference);

        final Boat boat = findBoat(boatReference, principal);

        boatService.delete(boat);

        return modelAndViewForMyListings(principal);
    }

    @RequestMapping(value = "/s/listings/{boatReference}/edit", method = RequestMethod.GET)
    public ModelAndView editListingPage(@PathVariable String boatReference, Principal principal) throws ResourceNotFoundException {

        LOG.info("editing listing with boat reference " + boatReference);

        final Boat boat = findBoat(boatReference, principal);

        final ModelAndView modelAndView = new ModelAndView("editListing");
        modelAndView.addObject("boat", boat);
        modelAndView.addObject("ryaSailCruisingLevels", referenceDataService.ryaSailCruisingLevels());
        modelAndView.addObject("sailingPurposes", referenceDataService.sailingPurposes());
        modelAndView.addObject("hullTypes", referenceDataService.hullTypes());

        return modelAndView;
    }

    @RequestMapping(value = "/s/listings/{boatReference}/edit", method = RequestMethod.POST)
    public ModelAndView editListingAction(@Valid Boat boatFromForm, @PathVariable String boatReference,
                                          BindingResult bindingResult, Principal principal) throws ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            LOG.info(bindingResult.toString());
            return new ModelAndView("editListing");
        }

        final Boat boatForUpdate = findBoat(boatReference, principal);
        boatForUpdate.setTitle(boatFromForm.getTitle());
        boatForUpdate.setMakeAndModel(boatFromForm.getMakeAndModel());
        boatForUpdate.setLength(boatFromForm.getLength());
        boatForUpdate.setUnitsImperial(boatFromForm.isUnitsImperial());
        boatForUpdate.setHullType(boatFromForm.getHullType());
        boatForUpdate.setDescription(boatFromForm.getDescription());
        boatForUpdate.setDateRelevantTo(boatFromForm.getDateRelevantTo());
        boatForUpdate.setMinimumRequiredLevel(boatFromForm.getMinimumRequiredLevel());
        boatForUpdate.setSailingPurpose(boatFromForm.getSailingPurpose());
        boatForUpdate.setSuspended(boatFromForm.isSuspended());

        final Boat updatedBoat = boatService.save(boatForUpdate);

        final ModelAndView modelAndView = new ModelAndView("redirect:/s/listings/mine?updated");
        modelAndView.addObject("boat", updatedBoat);
        return modelAndView;
    }

    @RequestMapping(value = { "/", "index" }, method = RequestMethod.GET)
    public ModelAndView index() {
        final ModelAndView modelAndView = new ModelAndView("index");
        addReferenceData(modelAndView);
        return modelAndView;
    }

    private void addModelAttributesForNewListing(ModelAndView modelAndView, User user) {
        modelAndView.addObject("allowedMoreListings", user.isAllowedMoreListings());
        addReferenceData(modelAndView);
    }

    private void addReferenceData(ModelAndView modelAndView) {
        modelAndView.addObject("ryaSailCruisingLevels", referenceDataService.ryaSailCruisingLevels());
        modelAndView.addObject("sailingPurposes", referenceDataService.sailingPurposes());
        modelAndView.addObject("hullTypes", referenceDataService.hullTypes());
        modelAndView.addObject("financialArrangements", referenceDataService.financialArrangements());
    }

    private void checkIsWithinListingLimit(User user) throws IllegalOperationException {
        if (user.getBoatListings().size() >=  user.getMaxListings()) {
            throw new IllegalOperationException("User is already at listing limit.");
        }
    }

    private Boat findBoat(String boatReference, Principal principal) throws ResourceNotFoundException {

        final User user = userService.findByUsername(principal.getName());

        for (Boat userBoat : user.getBoatListings()) {
            if (userBoat.getReference().equals(boatReference)) {
                return userBoat;
            }
        }

        throw new ResourceNotFoundException(String.format("No boat with reference %s found for user %s", boatReference, user.getUsername()));
    }

    private ModelAndView modelAndViewForMyListings(Principal principal) {
        final ModelAndView modelAndView = new ModelAndView("myListings");
        final User user = userService.findByUsername(principal.getName());
        modelAndView.addObject("boatListings", user.getBoatListings());
        return modelAndView;
    }
}
