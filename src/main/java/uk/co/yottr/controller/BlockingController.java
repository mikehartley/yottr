package uk.co.yottr.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.service.BlockedAddressService;

/**
 * The administrators' list of blocked addresses.
 */
@Controller
public class BlockingController {

    private BlockedAddressService blockedAddressService;

    @Autowired
    public BlockingController(BlockedAddressService blockedAddressService) {
        this.blockedAddressService = blockedAddressService;
    }

    @RequestMapping(value = "/admin/blocked", method = RequestMethod.GET)
    public ModelAndView blocked(HttpServletRequest request) {
        final ModelAndView modelAndView = new ModelAndView("adminBlocked");
        modelAndView.addObject("blocked", blockedAddressService.all());
        modelAndView.addObject("ownAddress", request.getRemoteAddr());
        return modelAndView;
    }

    @RequestMapping(value = "/admin/blocked", method = RequestMethod.POST)
    public String block(@RequestParam String address, @RequestParam(required = false) String note,
                        HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            blockedAddressService.block(address, note, request.getRemoteAddr());
        } catch (IllegalOperationException e) {
            redirectAttributes.addFlashAttribute("problem", e.getMessage());
            redirectAttributes.addFlashAttribute("address", address);
            redirectAttributes.addFlashAttribute("note", note);
        }
        return "redirect:/admin/blocked";
    }

    @RequestMapping(value = "/admin/blocked/{id}/delete", method = RequestMethod.POST)
    public String unblock(@PathVariable long id) {
        blockedAddressService.unblock(id);
        return "redirect:/admin/blocked";
    }
}
