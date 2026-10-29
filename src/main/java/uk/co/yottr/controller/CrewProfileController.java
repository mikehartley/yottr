package uk.co.yottr.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.form.CrewProfileForm;
import uk.co.yottr.form.CrewSearch;
import uk.co.yottr.service.CrewProfileService;
import uk.co.yottr.service.ReferenceDataService;

import java.security.Principal;

/**
 * A member's crew profile, and owners finding crew (#49).
 */
@Controller
public class CrewProfileController {

    private CrewProfileService crewProfileService;
    private ReferenceDataService referenceDataService;

    @Autowired
    public CrewProfileController(CrewProfileService crewProfileService, ReferenceDataService referenceDataService) {
        this.crewProfileService = crewProfileService;
        this.referenceDataService = referenceDataService;
    }

    @RequestMapping(value = "/s/crewProfile", method = RequestMethod.GET)
    public ModelAndView crewProfile(Principal principal) {
        final var profile = crewProfileService.profileOf(principal.getName());
        return profilePage(profile.map(CrewProfileForm::from).orElseGet(CrewProfileForm::new), profile.isPresent());
    }

    @RequestMapping(value = "/s/crewProfile", method = RequestMethod.POST)
    public ModelAndView saveCrewProfile(@Valid @ModelAttribute("profile") CrewProfileForm form, BindingResult bindingResult,
                                        Principal principal) {
        if (bindingResult.hasErrors()) {
            return profilePage(form, crewProfileService.profileOf(principal.getName()).isPresent());
        }
        crewProfileService.save(principal.getName(), form);
        return new ModelAndView("redirect:/s/crewProfile?saved");
    }

    @RequestMapping(value = "/s/crewProfile/delete", method = RequestMethod.POST)
    public String removeCrewProfile(Principal principal) {
        crewProfileService.remove(principal.getName());
        return "redirect:/s/crewProfile?removed";
    }

    @RequestMapping(value = "/s/crew/find", method = RequestMethod.GET)
    public ModelAndView findCrew(@ModelAttribute("search") CrewSearch search) {
        final ModelAndView modelAndView = new ModelAndView("findCrew");
        modelAndView.addObject("profiles", crewProfileService.search(search));
        modelAndView.addObject("sailingRegions", referenceDataService.sailingRegions());
        modelAndView.addObject("frequencies", referenceDataService.frequencies());
        modelAndView.addObject("ryaSailCruisingLevels", referenceDataService.ryaSailCruisingLevels());
        return modelAndView;
    }

    private ModelAndView profilePage(CrewProfileForm form, boolean exists) {
        final ModelAndView modelAndView = new ModelAndView("crewProfile");
        modelAndView.addObject("profile", form);
        modelAndView.addObject("exists", exists);
        modelAndView.addObject("sailingRegions", referenceDataService.sailingRegions());
        modelAndView.addObject("frequencies", referenceDataService.frequencies());
        modelAndView.addObject("ryaSailCruisingLevels", referenceDataService.ryaSailCruisingLevels());
        return modelAndView;
    }
}
