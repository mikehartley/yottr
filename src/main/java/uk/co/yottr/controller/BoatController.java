package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.form.BoatSearch;
import uk.co.yottr.form.ListingForm;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.security.Administrators;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.SavedBoatsService;
import uk.co.yottr.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;

@Controller
public class BoatController {

	private static final Logger LOG = LoggerFactory.getLogger(BoatController.class);

    private static final String REDIRECT_TO_MY_LISTINGS = "redirect:/s/listings/mine";

    private BoatService boatService;
    private UserService userService;
    private ReferenceDataService referenceDataService;
    private SavedBoatsService savedBoatsService;

    @Autowired
    public BoatController(BoatService boatService, UserService userService, ReferenceDataService referenceDataService,
                          SavedBoatsService savedBoatsService) {
        this.boatService = boatService;
        this.userService = userService;
        this.referenceDataService = referenceDataService;
        this.savedBoatsService = savedBoatsService;
    }

    @RequestMapping(value = "/s/listings/new", method = RequestMethod.GET)
	public ModelAndView newListingPage(Principal principal) {
        final User user = userService.findMember(principal.getName());

        ModelAndView modelAndView = new ModelAndView("newListing");

        modelAndView.addObject("boat", new ListingForm());
        addModelAttributesForNewListing(modelAndView, user);

        return modelAndView;
	}

    // The listing forms bind to ListingForm, never to Boat, so a request can only set the fields on the form.

    /**
     * BoatService refuses the listing with IllegalOperationException, which is a 403, if the member is at their limit.
     */
    @RequestMapping(value = "/s/listings/new", method = RequestMethod.POST)
	public ModelAndView newListingAction(@Valid @ModelAttribute("boat") ListingForm listingForm, BindingResult bindingResult,
                                         Principal principal) {
        if (bindingResult.hasErrors()) {
            LOG.info(bindingResult.toString());
            ModelAndView modelAndView = new ModelAndView("newListing");
            addModelAttributesForNewListing(modelAndView, userService.findMember(principal.getName()));
            return modelAndView;
        }

        final Boat boat = boatService.create(principal.getName(), listingForm);

        ModelAndView modelAndView = new ModelAndView("newListingSuccess");
        modelAndView.addObject("boat", boat);
        return modelAndView;
	}

    /**
     * The boats members can see, newest first, narrowed by the search. htmx asks for this as the search changes,
     * and gets back only the results.
     */
    @RequestMapping(value = "/s/listings/all", method = RequestMethod.GET)
    public ModelAndView listBoats(@ModelAttribute("search") BoatSearch search,
                                  @SortDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                  HttpServletRequest request, HttpServletResponse response, Principal principal) {
        final boolean resultsOnly = Htmx.wantsPartOfPage(request, response);
        final ModelAndView modelAndView = new ModelAndView(resultsOnly ? "boatList :: results" : "boatList");

        final Page<Boat> boatPages = boatService.search(search, pageable);
        modelAndView.addObject("wrapper", new PageWrapper<>(boatPages));
        modelAndView.addObject("searchQuery", search.toQueryString());
        modelAndView.addObject("savedReferences", savedBoatsService.savedReferences(principal.getName()));
        if (!resultsOnly) {
            addSearchReferenceData(modelAndView);
        }

        return modelAndView;
    }

    @RequestMapping(value = "/s/listings/mine", method = RequestMethod.GET)
    public ModelAndView myListings(Principal principal) {
        LOG.info("My listings page");

        final ModelAndView modelAndView = new ModelAndView("myListings");
        modelAndView.addObject("boatListings", boatService.findListings(principal.getName()));
        return modelAndView;
    }

    /**
     * A listing's own page, with its owner's contact details, which any signed-in member may see. A listing that
     * isn't visible (suspended, withdrawn, or its owner disabled) is reported as not found, except to its owner and
     * to administrators, who also get the means to withdraw or delete it.
     */
    @RequestMapping(value = "/s/listings/{boatReference}", method = RequestMethod.GET)
    public ModelAndView listingPage(@PathVariable String boatReference, Principal principal) {
        final boolean administrator = Administrators.isAdministrator(principal);
        final Boat boat = boatService.findListingToView(boatReference, principal.getName(), administrator)
                .orElseThrow(() -> new ResourceNotFoundException("No listing with reference " + boatReference));

        final ModelAndView modelAndView = new ModelAndView("listingPage");
        modelAndView.addObject("boat", boat);
        modelAndView.addObject("own", boat.getOwner().getUsername().equals(principal.getName()));
        modelAndView.addObject("administrator", administrator);
        modelAndView.addObject("saved", savedBoatsService.savedReferences(principal.getName()).contains(boatReference));
        return modelAndView;
    }

    /**
     * The listings the member has saved, including any that other members can no longer see.
     */
    @RequestMapping(value = "/s/listings/saved", method = RequestMethod.GET)
    public ModelAndView savedBoats(Principal principal) {
        final ModelAndView modelAndView = new ModelAndView("savedBoats");
        modelAndView.addObject("boats", savedBoatsService.savedBoats(principal.getName()));
        return modelAndView;
    }

    /**
     * Saves a listing the member can see, and goes back to the page they saved it from.
     */
    @RequestMapping(value = "/s/listings/{boatReference}/save", method = RequestMethod.POST)
    public String saveListing(@PathVariable String boatReference, @RequestParam(required = false) String next, Principal principal) {
        boatService.findListingToView(boatReference, principal.getName(), Administrators.isAdministrator(principal))
                .orElseThrow(() -> new ResourceNotFoundException("No listing with reference " + boatReference));

        savedBoatsService.save(principal.getName(), boatReference);

        return "redirect:" + sameSitePath(next);
    }

    /**
     * Unsaves a listing, which works even if it can no longer be seen, so that it can be cleared from Saved.
     */
    @RequestMapping(value = "/s/listings/{boatReference}/unsave", method = RequestMethod.POST)
    public String unsaveListing(@PathVariable String boatReference, @RequestParam(required = false) String next, Principal principal) {
        savedBoatsService.unsave(principal.getName(), boatReference);

        return "redirect:" + sameSitePath(next);
    }

    /**
     * Where to go back to after saving: a members' page on this site, or Saved if it isn't one, so that the
     * parameter can't send a member somewhere else.
     */
    static String sameSitePath(String next) {
        return next != null && next.startsWith("/s/") && !next.contains("//") && !next.contains("\\") ? next : "/s/listings/saved";
    }

    // Changes are POSTs, which need the CSRF token, and redirect afterwards so that a refresh doesn't repeat them.

    @RequestMapping(value = "/s/listings/{boatReference}/suspended/flip", method = RequestMethod.POST)
    public String flipSuspended(@PathVariable String boatReference, Principal principal) {

        LOG.info("updating suspended status for boat listing with reference {}", boatReference);

        boatService.flipSuspended(findListing(boatReference, principal));

        return REDIRECT_TO_MY_LISTINGS;
    }

    @RequestMapping(value = "/s/listings/{boatReference}/delete", method = RequestMethod.POST)
    public String deleteListing(@PathVariable String boatReference, Principal principal) {

        LOG.info("deleting listing with boat reference {}", boatReference);

        boatService.delete(findListing(boatReference, principal));

        return REDIRECT_TO_MY_LISTINGS;
    }

    @RequestMapping(value = "/s/listings/{boatReference}/edit", method = RequestMethod.GET)
    public ModelAndView editListingPage(@PathVariable String boatReference, Principal principal) {

        LOG.info("editing listing with boat reference {}", boatReference);

        final Boat boat = findListing(boatReference, principal);

        final ModelAndView modelAndView = new ModelAndView("editListing");
        modelAndView.addObject("boat", ListingForm.from(boat));
        modelAndView.addObject("boatReference", boatReference);
        addListingFormReferenceData(modelAndView);

        return modelAndView;
    }

    @RequestMapping(value = "/s/listings/{boatReference}/edit", method = RequestMethod.POST)
    public ModelAndView editListingAction(@Valid @ModelAttribute("boat") ListingForm listingForm, BindingResult bindingResult,
                                          @PathVariable String boatReference, Principal principal) {
        if (bindingResult.hasErrors()) {
            LOG.info(bindingResult.toString());
            final ModelAndView modelAndView = new ModelAndView("editListing");
            modelAndView.addObject("boatReference", boatReference);
            addListingFormReferenceData(modelAndView);
            return modelAndView;
        }

        final Boat boatForUpdate = findListing(boatReference, principal);

        // suspended is not on the form; it is changed with /suspended/flip
        final Boat updatedBoat = boatService.update(boatForUpdate, listingForm);

        final ModelAndView modelAndView = new ModelAndView("redirect:/s/listings/mine?updated");
        modelAndView.addObject("boat", updatedBoat);
        return modelAndView;
    }

    @RequestMapping(value = { "/", "index" }, method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    private void addModelAttributesForNewListing(ModelAndView modelAndView, User user) {
        modelAndView.addObject("allowedMoreListings", user.isAllowedMoreListings());
        addListingFormReferenceData(modelAndView);
    }

    private void addListingFormReferenceData(ModelAndView modelAndView) {
        addReferenceData(modelAndView);
        modelAndView.addObject("vesselTypes", referenceDataService.vesselTypes());
        modelAndView.addObject("rolesRequired", referenceDataService.rolesRequired());
        modelAndView.addObject("frequencies", referenceDataService.frequencies());
        modelAndView.addObject("smokingOptions", referenceDataService.smokingOptions());
        modelAndView.addObject("travelExpensesOptions", referenceDataService.travelExpenses());
        modelAndView.addObject("courseLevels", referenceDataService.courseLevels());
    }

    private void addSearchReferenceData(ModelAndView modelAndView) {
        modelAndView.addObject("sailingPurposes", referenceDataService.sailingPurposes());
        modelAndView.addObject("frequencies", referenceDataService.frequencies());
        modelAndView.addObject("financialArrangements", referenceDataService.financialArrangements());
        modelAndView.addObject("ryaSailCruisingLevels", referenceDataService.ryaSailCruisingLevels());
        modelAndView.addObject("postedWithinDays", BoatSearch.POSTED_WITHIN_DAYS);
    }

    private void addReferenceData(ModelAndView modelAndView) {
        modelAndView.addObject("ryaSailCruisingLevels", referenceDataService.ryaSailCruisingLevels());
        modelAndView.addObject("sailingPurposes", referenceDataService.sailingPurposes());
        modelAndView.addObject("hullTypes", referenceDataService.hullTypes());
        modelAndView.addObject("financialArrangements", referenceDataService.financialArrangements());
    }

    /**
     * The signed-in member's listing with this reference: someone else's is reported as not found.
     */
    private Boat findListing(String boatReference, Principal principal) {
        return boatService.findListing(boatReference, principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No boat with reference %s found for user %s", boatReference, principal.getName())));
    }
}
