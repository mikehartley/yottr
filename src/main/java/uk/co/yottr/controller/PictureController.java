package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.ListingPicture.Picture;
import uk.co.yottr.security.Administrators;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ListingPictureService;
import uk.co.yottr.service.PictureProcessor;
import uk.co.yottr.service.PictureProcessor.UnusablePictureException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * The pictures on listings: the owner's page for adding and arranging them, the pictures themselves, and taking
 * one down for an administrator.
 */
@Controller
public class PictureController {

    private static final Logger LOG = LoggerFactory.getLogger(PictureController.class);

    // A picture never changes once added (a new one gets a new ID), so a member's browser can keep it, but only for a
    // day: it may be taken down, or its listing hidden, and a browser that has it won't ask again until then.
    private static final CacheControl CACHE_PICTURE = CacheControl.maxAge(Duration.ofDays(1)).cachePrivate();

    private BoatService boatService;
    private ListingPictureService listingPictureService;
    private PictureProcessor pictureProcessor;

    @Autowired
    public PictureController(BoatService boatService, ListingPictureService listingPictureService,
                             PictureProcessor pictureProcessor) {
        this.boatService = boatService;
        this.listingPictureService = listingPictureService;
        this.pictureProcessor = pictureProcessor;
    }

    @RequestMapping(value = "/s/listings/{boatReference}/pictures", method = RequestMethod.GET)
    public ModelAndView picturesPage(@PathVariable String boatReference, Principal principal) {
        final Boat boat = findListing(boatReference, principal);

        final ModelAndView modelAndView = new ModelAndView("listingPictures");
        modelAndView.addObject("boat", boat);
        modelAndView.addObject("pictures", listingPictureService.pictures(boat));
        modelAndView.addObject("room", listingPictureService.roomFor(boat));
        modelAndView.addObject("maxPictures", ListingPictureService.MAX_PICTURES);
        return modelAndView;
    }

    // Changes are POSTs, which need the CSRF token, and redirect afterwards so that a refresh doesn't repeat them.

    /**
     * Adds the chosen pictures, as many as there's room for. Any that can't be added are reported by file name,
     * and the rest are added all the same.
     */
    @RequestMapping(value = "/s/listings/{boatReference}/pictures", method = RequestMethod.POST)
    public String addPictures(@PathVariable String boatReference,
                              @RequestParam(name = "pictures", required = false) List<MultipartFile> uploads,
                              Principal principal, RedirectAttributes redirectAttributes) {
        final Boat boat = findListing(boatReference, principal);
        // with no file chosen, a browser still sends the field, empty
        final List<MultipartFile> chosen = uploads == null ? List.of() : uploads.stream().filter(upload -> !upload.isEmpty()).toList();

        final int room = listingPictureService.roomFor(boat);
        final List<Picture> pictures = new ArrayList<>();
        final List<String> problems = new ArrayList<>();
        int noRoom = 0;
        for (MultipartFile upload : chosen) {
            if (pictures.size() == room) {
                noRoom++;
                continue;
            }
            try {
                pictures.add(pictureProcessor.process(upload.getBytes()));
            } catch (UnusablePictureException e) {
                LOG.info("picture {} for listing {} refused: {}", upload.getOriginalFilename(), boatReference, e.getMessage());
                problems.add(fileName(upload) + " " + e.getMessage() + ".");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        final int added = listingPictureService.add(boat, pictures);
        noRoom += pictures.size() - added;
        if (noRoom > 0) {
            problems.add("A listing can have up to " + ListingPictureService.MAX_PICTURES + " pictures, so "
                    + (noRoom == 1 ? "1 picture wasn't" : noRoom + " pictures weren't") + " added.");
        }
        if (chosen.isEmpty()) {
            problems.add("Choose a picture to add.");
        }

        redirectAttributes.addFlashAttribute("added", added);
        redirectAttributes.addFlashAttribute("problems", problems);
        return picturesPageOf(boatReference);
    }

    @RequestMapping(value = "/s/listings/{boatReference}/pictures/{pictureId}/first", method = RequestMethod.POST)
    public String makeFirst(@PathVariable String boatReference, @PathVariable long pictureId, Principal principal) {
        listingPictureService.makeFirst(findListing(boatReference, principal), pictureId);
        return picturesPageOf(boatReference);
    }

    @RequestMapping(value = "/s/listings/{boatReference}/pictures/{pictureId}/delete", method = RequestMethod.POST)
    public String removePicture(@PathVariable String boatReference, @PathVariable long pictureId, Principal principal) {
        listingPictureService.remove(findListing(boatReference, principal), pictureId);
        return picturesPageOf(boatReference);
    }

    @RequestMapping(value = "/admin/listings/{boatReference}/pictures/{pictureId}/delete", method = RequestMethod.POST)
    public String takeDownPicture(@PathVariable String boatReference, @PathVariable long pictureId) {

        LOG.info("an administrator is taking down picture {} on listing {}", pictureId, boatReference);

        final Boat boat = boatService.findAnyListing(boatReference)
                .orElseThrow(() -> new ResourceNotFoundException("No listing with reference " + boatReference));
        listingPictureService.takeDown(boat, pictureId);

        return "redirect:/s/listings/" + boatReference;
    }

    /**
     * A picture, to anyone who may see its listing.
     */
    @RequestMapping(value = "/s/listings/{boatReference}/pictures/{pictureId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> picture(@PathVariable String boatReference, @PathVariable long pictureId, Principal principal) {
        return jpeg(boatReference, pictureId, principal, listingPictureService::image);
    }

    @RequestMapping(value = "/s/listings/{boatReference}/pictures/{pictureId}/thumbnail", method = RequestMethod.GET)
    public ResponseEntity<byte[]> thumbnail(@PathVariable String boatReference, @PathVariable long pictureId, Principal principal) {
        return jpeg(boatReference, pictureId, principal, listingPictureService::thumbnail);
    }

    private ResponseEntity<byte[]> jpeg(String boatReference, long pictureId, Principal principal,
                                        BiFunction<Boat, Long, Optional<byte[]>> find) {
        final Boat boat = boatService.findListingToView(boatReference, principal.getName(), Administrators.isAdministrator(principal))
                .orElseThrow(() -> new ResourceNotFoundException("No listing with reference " + boatReference));
        final byte[] jpeg = find.apply(boat, pictureId)
                .orElseThrow(() -> new ResourceNotFoundException("No picture " + pictureId + " on listing " + boatReference));
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).cacheControl(CACHE_PICTURE).body(jpeg);
    }

    private static String picturesPageOf(String boatReference) {
        return "redirect:/s/listings/" + boatReference + "/pictures";
    }

    private static String fileName(MultipartFile upload) {
        final String name = upload.getOriginalFilename();
        return name == null || name.isBlank() ? "A picture" : name;
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
