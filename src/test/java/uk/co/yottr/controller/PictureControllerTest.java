package uk.co.yottr.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import uk.co.yottr.model.Boat;
import uk.co.yottr.repository.ListingPictureRepository.PictureSummary;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.ListingPictureService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.yottr.builder.TestPictures.jpeg;
import static uk.co.yottr.builder.UserBuilder.aUser;

public class PictureControllerTest extends AbstractControllerTest {

    @Autowired
    private BoatService mockBoatService;

    @Autowired
    private ListingPictureService mockListingPictureService;

    private Boat boat;
    private String picturesPage;

    @BeforeEach
    public void createAListing() {
        reset(mockBoatService, mockListingPictureService);
        boat = aUser().withUsername("alice").withBoat().build().getBoatListings().get(0);
        picturesPage = "/s/listings/" + boat.getReference() + "/pictures";
    }

    @AfterEach
    public void afterEachTest() {
        verifyNoMoreInteractions(mockBoatService, mockListingPictureService);
    }

    @Test
    public void theOwnerSeesTheirPicturesAndCanAddMore() throws Exception {
        whenAliceOwnsTheListing();
        final List<PictureSummary> pictures = List.of(new PictureSummary(7L, 1600, 1200, 480, 360), new PictureSummary(8L, 1600, 1200, 480, 360));
        when(mockListingPictureService.pictures(boat)).thenReturn(pictures);
        when(mockListingPictureService.roomFor(boat)).thenReturn(4);

        mockMvc.perform(get(picturesPage).principal(() -> "alice"))
                .andExpect(status().isOk())
                .andExpect(view().name("listingPictures"))
                .andExpect(model().attribute("pictures", pictures))
                .andExpect(content().string(containsString(picturesPage + "/7/thumbnail")))
                .andExpect(content().string(containsString("On the card")))
                .andExpect(content().string(containsString(picturesPage + "/8/first")))
                .andExpect(content().string(not(containsString(picturesPage + "/7/first"))))
                .andExpect(content().string(containsString("there's room for <span>4</span> more")))
                .andExpect(content().string(containsString("id=\"add-pictures-form\"")));

        verify(mockBoatService).findListing(boat.getReference(), "alice");
        verify(mockListingPictureService).pictures(boat);
        verify(mockListingPictureService).roomFor(boat);
    }

    @Test
    public void aListingWithSixPicturesHasNoRoomForMore() throws Exception {
        whenAliceOwnsTheListing();
        when(mockListingPictureService.roomFor(boat)).thenReturn(0);

        mockMvc.perform(get(picturesPage).principal(() -> "alice"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Remove one to add another.")))
                .andExpect(content().string(not(containsString("id=\"add-pictures-form\""))));

        verify(mockBoatService).findListing(boat.getReference(), "alice");
        verify(mockListingPictureService).pictures(boat);
        verify(mockListingPictureService).roomFor(boat);
    }

    @Test
    public void onlyTheOwnerHasThePicturesPage() throws Exception {
        when(mockBoatService.findListing(boat.getReference(), "dave")).thenReturn(Optional.empty());

        mockMvc.perform(get(picturesPage).principal(() -> "dave"))
                .andExpect(status().isNotFound());

        verify(mockBoatService).findListing(boat.getReference(), "dave");
    }

    @Test
    public void picturesThatCanBeReadAreAddedAndTheOthersReported() throws Exception {
        whenAliceOwnsTheListing();
        when(mockListingPictureService.roomFor(boat)).thenReturn(6);
        when(mockListingPictureService.add(eq(boat), anyList())).thenAnswer(invocation -> invocation.<List<?>>getArgument(1).size());

        mockMvc.perform(multipart(picturesPage).file(upload("cockpit.jpg", jpeg(40, 30))).file(upload("notes.txt", "Not a picture".getBytes()))
                        .principal(() -> "alice"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(picturesPage))
                .andExpect(flash().attribute("added", 1))
                .andExpect(flash().attribute("problems", List.of("notes.txt isn't a JPEG, PNG or WebP picture.")));

        verify(mockBoatService).findListing(boat.getReference(), "alice");
        verify(mockListingPictureService).roomFor(boat);
        verify(mockListingPictureService).add(eq(boat), argThat(pictures -> pictures.size() == 1));
    }

    @Test
    public void onlyAsManyPicturesAreAddedAsThereIsRoomFor() throws Exception {
        whenAliceOwnsTheListing();
        when(mockListingPictureService.roomFor(boat)).thenReturn(1);
        when(mockListingPictureService.add(eq(boat), anyList())).thenAnswer(invocation -> invocation.<List<?>>getArgument(1).size());

        mockMvc.perform(multipart(picturesPage).file(upload("bow.jpg", jpeg(40, 30))).file(upload("stern.jpg", jpeg(40, 30)))
                        .principal(() -> "alice"))
                .andExpect(redirectedUrl(picturesPage))
                .andExpect(flash().attribute("added", 1))
                .andExpect(flash().attribute("problems", List.of("A listing can have up to 6 pictures, so 1 picture wasn't added.")));

        verify(mockBoatService).findListing(boat.getReference(), "alice");
        verify(mockListingPictureService).roomFor(boat);
        verify(mockListingPictureService).add(eq(boat), argThat(pictures -> pictures.size() == 1));
    }

    @Test
    public void addingWithNoPictureChosenSaysToChooseOne() throws Exception {
        whenAliceOwnsTheListing();
        when(mockListingPictureService.roomFor(boat)).thenReturn(6);

        // a browser sends the field, empty, when no file is chosen
        mockMvc.perform(multipart(picturesPage).file(upload("", new byte[0])).principal(() -> "alice"))
                .andExpect(redirectedUrl(picturesPage))
                .andExpect(flash().attribute("added", 0))
                .andExpect(flash().attribute("problems", List.of("Choose a picture to add.")));

        verify(mockBoatService).findListing(boat.getReference(), "alice");
        verify(mockListingPictureService).roomFor(boat);
        verify(mockListingPictureService).add(boat, List.of());
    }

    @Test
    public void picturesCantBeAddedToSomeoneElsesListing() throws Exception {
        when(mockBoatService.findListing(boat.getReference(), "dave")).thenReturn(Optional.empty());

        mockMvc.perform(multipart(picturesPage).file(upload("bow.jpg", jpeg(40, 30))).principal(() -> "dave"))
                .andExpect(status().isNotFound());

        verify(mockBoatService).findListing(boat.getReference(), "dave");
    }

    @Test
    public void theOwnerMakesAPictureFirstAndRemovesOne() throws Exception {
        whenAliceOwnsTheListing();

        mockMvc.perform(post(picturesPage + "/8/first").principal(() -> "alice"))
                .andExpect(redirectedUrl(picturesPage));
        mockMvc.perform(post(picturesPage + "/7/delete").principal(() -> "alice"))
                .andExpect(redirectedUrl(picturesPage));

        verify(mockBoatService, times(2)).findListing(boat.getReference(), "alice");
        verify(mockListingPictureService).makeFirst(boat, 8L);
        verify(mockListingPictureService).remove(boat, 7L);
    }

    @Test
    public void aPictureIsServedToAnyoneWhoMaySeeTheListingAndKeptByTheirBrowser() throws Exception {
        final byte[] image = {1, 2, 3};
        final byte[] thumbnail = {4, 5};
        when(mockBoatService.findListingToView(boat.getReference(), "dave", false)).thenReturn(Optional.of(boat));
        when(mockListingPictureService.image(boat, 7L)).thenReturn(Optional.of(image));
        when(mockListingPictureService.thumbnail(boat, 7L)).thenReturn(Optional.of(thumbnail));

        mockMvc.perform(get(picturesPage + "/7").principal(() -> "dave"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(header().string("Cache-Control", "max-age=86400, private"))
                .andExpect(content().bytes(image));
        mockMvc.perform(get(picturesPage + "/7/thumbnail").principal(() -> "dave"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(thumbnail));

        verify(mockBoatService, times(2)).findListingToView(boat.getReference(), "dave", false);
        verify(mockListingPictureService).image(boat, 7L);
        verify(mockListingPictureService).thumbnail(boat, 7L);
    }

    @Test
    public void aPictureIsNotFoundIfItsListingCantBeSeenOrItIsntOnIt() throws Exception {
        when(mockBoatService.findListingToView("hidden", "dave", false)).thenReturn(Optional.empty());
        when(mockBoatService.findListingToView(boat.getReference(), "dave", false)).thenReturn(Optional.of(boat));
        when(mockListingPictureService.image(boat, 99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/s/listings/hidden/pictures/7").principal(() -> "dave"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(picturesPage + "/99").principal(() -> "dave"))
                .andExpect(status().isNotFound());

        verify(mockBoatService).findListingToView("hidden", "dave", false);
        verify(mockBoatService).findListingToView(boat.getReference(), "dave", false);
        verify(mockListingPictureService).image(boat, 99L);
    }

    @Test
    public void anAdministratorTakesDownAPicture() throws Exception {
        when(mockBoatService.findAnyListing(boat.getReference())).thenReturn(Optional.of(boat));

        mockMvc.perform(post("/admin/listings/" + boat.getReference() + "/pictures/7/delete")
                        .principal(new TestingAuthenticationToken("ada", null, "FREE", "ADMIN")))
                .andExpect(redirectedUrl("/s/listings/" + boat.getReference()));

        verify(mockBoatService).findAnyListing(boat.getReference());
        verify(mockListingPictureService).takeDown(boat, 7L);
    }

    private void whenAliceOwnsTheListing() {
        when(mockBoatService.findListing(boat.getReference(), "alice")).thenReturn(Optional.of(boat));
    }

    private static MockMultipartFile upload(String fileName, byte[] content) {
        return new MockMultipartFile("pictures", fileName, "application/octet-stream", content);
    }
}
