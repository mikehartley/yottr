package uk.co.yottr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.Boat;
import uk.co.yottr.security.Administrators;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.CrewService;

import java.security.Principal;

/**
 * Joining a boat's crew from its listing, the owner accepting or removing crew, and the boats a member crews on.
 * The changes are POSTs that go back to the listing's crew panel.
 */
@Controller
public class CrewController {

    private BoatService boatService;
    private CrewService crewService;

    @Autowired
    public CrewController(BoatService boatService, CrewService crewService) {
        this.boatService = boatService;
        this.crewService = crewService;
    }

    @RequestMapping(value = "/s/listings/{boatReference}/crew", method = RequestMethod.POST)
    public String ask(@PathVariable String boatReference, Principal principal) {
        final Boat boat = boatService.findListingToView(boatReference, principal.getName(), Administrators.isAdministrator(principal))
                .orElseThrow(() -> new ResourceNotFoundException("No listing with reference " + boatReference));
        crewService.ask(boat, principal.getName());
        return backToTheCrew(boatReference);
    }

    /**
     * Leaving the crew, or withdrawing the asking. It works even if the listing can no longer be seen.
     */
    @RequestMapping(value = "/s/listings/{boatReference}/crew/leave", method = RequestMethod.POST)
    public String leave(@PathVariable String boatReference, Principal principal) {
        final Boat boat = boatService.findAnyListing(boatReference)
                .orElseThrow(() -> new ResourceNotFoundException("No listing with reference " + boatReference));
        crewService.leave(boat, principal.getName());
        return "redirect:/s/crew";
    }

    @RequestMapping(value = "/s/listings/{boatReference}/crew/{crewMemberId}/accept", method = RequestMethod.POST)
    public String accept(@PathVariable String boatReference, @PathVariable long crewMemberId, Principal principal) {
        crewService.accept(ownListing(boatReference, principal), crewMemberId);
        return backToTheCrew(boatReference);
    }

    @RequestMapping(value = "/s/listings/{boatReference}/crew/{crewMemberId}/remove", method = RequestMethod.POST)
    public String remove(@PathVariable String boatReference, @PathVariable long crewMemberId, Principal principal) {
        crewService.remove(ownListing(boatReference, principal), crewMemberId);
        return backToTheCrew(boatReference);
    }

    /**
     * The boats the member crews on, or has asked to.
     */
    @RequestMapping(value = "/s/crew", method = RequestMethod.GET)
    public ModelAndView crewing(Principal principal) {
        final ModelAndView modelAndView = new ModelAndView("crewing");
        modelAndView.addObject("places", crewService.crewing(principal.getName()));
        return modelAndView;
    }

    private Boat ownListing(String boatReference, Principal principal) {
        return boatService.findListing(boatReference, principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No boat with reference %s found for user %s", boatReference, principal.getName())));
    }

    private static String backToTheCrew(String boatReference) {
        return "redirect:/s/listings/" + boatReference + "#crew";
    }
}
