package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.security.Roles;
import uk.co.yottr.tempDatastore.Database;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;

@Controller
public class BoatController {

	private static final Logger LOG = LoggerFactory.getLogger(BoatController.class);

    private final Database database = new Database();

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
		database.getBoats().add(boat);
		return "newListingSuccess";
	}

    @RequestMapping(value = "/s/listings/all", method = RequestMethod.GET)
    public ModelAndView listBoats() {
        LOG.info("All listings page");

        ModelAndView modelAndView = new ModelAndView("boatList");
        modelAndView.addObject("boats", database.getBoats());

        return modelAndView;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signup() {
        LOG.info("Signup page (GET)");

        ModelAndView modelAndView = new ModelAndView("signup");
        modelAndView.addObject("user", new User());

        return modelAndView;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupAction(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            LOG.info(bindingResult.toString());
            LOG.info("Returning signup.jsp page from signupAction");
            return "signup";
        }
        LOG.info("Returning signupSuccess.jsp page");
        model.addAttribute("user", user);
        user.setAuthorities(Arrays.asList(new SimpleGrantedAuthority(Roles.FREE.name())));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        database.getUsers().put(user.getUsername(), user);

        LOG.info("Signed up new user: " + user);

        return "signupSuccess";
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
}
