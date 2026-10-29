package uk.co.yottr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.yottr.config.TestUsers;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.CrewService;
import uk.co.yottr.service.ListingPictureService;
import uk.co.yottr.service.PictureProcessor;
import uk.co.yottr.service.ReferenceDataService;
import uk.co.yottr.service.SavedBoatsService;
import uk.co.yottr.service.UserService;

/**
 * Controllers with mocked services and no security filters, as the old ControllerTestConfig set up. The picture
 * processor and the test users are the real ones, which need nothing else.
 */
@WebMvcTest
@Import({PictureProcessor.class, TestUsers.class})
@AutoConfigureMockMvc(addFilters = false)
public abstract class AbstractControllerTest {

    @Autowired
    MockMvc mockMvc;

    // needed by UsernameAvailableValidator
    @MockitoBean
    UserRepository mockUserRepository;

    @MockitoBean
    BoatService mockBoatService;

    @MockitoBean
    UserService mockUserService;

    @MockitoBean
    ReferenceDataService mockReferenceDataService;

    @MockitoBean
    LoggersEndpoint mockLoggersEndpoint;

    @MockitoBean
    SavedBoatsService mockSavedBoatsService;

    @MockitoBean
    ListingPictureService mockListingPictureService;

    @MockitoBean
    CrewService mockCrewService;
}
