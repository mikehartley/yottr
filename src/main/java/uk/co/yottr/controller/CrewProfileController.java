package uk.co.yottr.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.yottr.form.CrewProfileForm;
import uk.co.yottr.form.CrewSearch;
import uk.co.yottr.service.CrewProfileService;
import uk.co.yottr.service.ReferenceDataService;

import java.security.Principal;

/**
 * A member's crew profile, which is part of My Details, and owners finding crew (#49).
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

    /**
     * The crew profile is part of My Details.
     */
    @RequestMapping(value = "/s/crewProfile", method = RequestMethod.GET)
    public String crewProfile() {
        return "redirect:/s/myDetails#crew-profile";
    }

    /**
     * Saves the profile and goes back to it in My Details; if it can't be saved, it goes back there as it was
     * filled in, with its errors (UserController shows it).
     */
    @RequestMapping(value = "/s/crewProfile", method = RequestMethod.POST)
    public String saveCrewProfile(@Valid @ModelAttribute("profile") CrewProfileForm form, BindingResult bindingResult,
                                  Principal principal, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("profile", form);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "profile", bindingResult);
            return "redirect:/s/myDetails#crew-profile";
        }
        crewProfileService.save(principal.getName(), form);
        return "redirect:/s/myDetails?profileSaved#crew-profile";
    }

    @RequestMapping(value = "/s/crewProfile/delete", method = RequestMethod.POST)
    public String removeCrewProfile(Principal principal) {
        crewProfileService.remove(principal.getName());
        return "redirect:/s/myDetails?profileRemoved#crew-profile";
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
}
