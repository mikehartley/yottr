package uk.co.yottr.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.ListingPicture.Picture;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.ListingPictureRepository.PictureSummary;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.TestPictures.jpeg;
import static uk.co.yottr.builder.UserBuilder.aUser;

@SpringBootTest
@Import(TestcontainersConfig.class)
public class ListingPictureServiceIT {

    @Autowired
    private ListingPictureService listingPictureService;

    @Autowired
    private PictureProcessor pictureProcessor;

    @Autowired
    private BoatService boatService;

    @Autowired
    private UserService userService;

    private Boat boat;
    private Picture picture;

    @BeforeEach
    public void createAListing() {
        final User owner = userService.save(aUser().withUsername("owner" + secure().nextAlphanumeric(10)).build(), false);
        boat = boatService.save(aBoat().withOwner(owner).build());
        picture = pictureProcessor.process(jpeg(64, 48));
    }

    @Test
    public void picturesAreAddedInOrderAndTheFirstIsOnTheCard() {
        assertNull(boatService.findByReference(boat.getReference()).getCoverPictureId());

        assertEquals(2, listingPictureService.add(boat, List.of(picture, picture)));
        final List<PictureSummary> pictures = listingPictureService.pictures(boat);

        assertEquals(2, pictures.size());
        assertEquals(64, pictures.get(0).width());
        assertEquals(48, pictures.get(0).thumbnailHeight());
        assertEquals(pictures.get(0).id(), boatService.findByReference(boat.getReference()).getCoverPictureId());
        assertArrayEquals(picture.image().jpeg(), listingPictureService.image(boat, pictures.get(1).id()).orElseThrow());
        assertArrayEquals(picture.thumbnail().jpeg(), listingPictureService.thumbnail(boat, pictures.get(1).id()).orElseThrow());
    }

    @Test
    public void aListingHasUpToSixPictures() {
        assertEquals(4, listingPictureService.add(boat, Collections.nCopies(4, picture)));
        assertEquals(2, listingPictureService.roomFor(boat));

        assertEquals(2, listingPictureService.add(boat, Collections.nCopies(3, picture)));
        assertEquals(0, listingPictureService.add(boat, List.of(picture)));
        assertEquals(ListingPictureService.MAX_PICTURES, listingPictureService.pictures(boat).size());
        assertEquals(0, listingPictureService.roomFor(boat));
    }

    @Test
    public void anyPictureCanBeMadeFirst() {
        listingPictureService.add(boat, List.of(picture, picture, picture));
        final List<Long> ids = listingPictureService.pictures(boat).stream().map(PictureSummary::id).toList();

        listingPictureService.makeFirst(boat, ids.get(2));
        assertEquals(List.of(ids.get(2), ids.get(0), ids.get(1)), idsOf(boat));
        assertEquals(ids.get(2), boatService.findByReference(boat.getReference()).getCoverPictureId());

        listingPictureService.makeFirst(boat, ids.get(2));
        assertEquals(List.of(ids.get(2), ids.get(0), ids.get(1)), idsOf(boat), "already first");

        // one added afterwards goes last
        listingPictureService.add(boat, List.of(picture));
        assertEquals(ids.get(2), idsOf(boat).get(0));
    }

    @Test
    public void removingAPictureMakesRoomForAnother() {
        listingPictureService.add(boat, Collections.nCopies(ListingPictureService.MAX_PICTURES, picture));
        final Long first = idsOf(boat).get(0);

        listingPictureService.remove(boat, first);
        assertEquals(1, listingPictureService.roomFor(boat));
        assertTrue(listingPictureService.image(boat, first).isEmpty());

        listingPictureService.takeDown(boat, idsOf(boat).get(0));
        assertEquals(2, listingPictureService.roomFor(boat));
    }

    @Test
    public void aPictureBelongsOnlyToItsOwnListing() {
        final Boat other = boatService.save(aBoat().withOwner(boat.getOwner()).build());
        listingPictureService.add(other, List.of(picture));
        final Long othersPicture = idsOf(other).get(0);

        assertTrue(listingPictureService.image(boat, othersPicture).isEmpty());
        assertTrue(listingPictureService.thumbnail(boat, othersPicture).isEmpty());
        assertThrows(ResourceNotFoundException.class, () -> listingPictureService.remove(boat, othersPicture));
        assertThrows(ResourceNotFoundException.class, () -> listingPictureService.takeDown(boat, othersPicture));
        assertThrows(ResourceNotFoundException.class, () -> listingPictureService.makeFirst(boat, othersPicture));
        assertEquals(List.of(othersPicture), idsOf(other));
    }

    @Test
    public void deletingAListingDeletesItsPictures() {
        listingPictureService.add(boat, List.of(picture));
        final Long id = idsOf(boat).get(0);

        boatService.delete(boatService.findByReference(boat.getReference()));

        assertTrue(listingPictureService.image(boat, id).isEmpty());
    }

    private List<Long> idsOf(Boat boat) {
        return listingPictureService.pictures(boat).stream().map(PictureSummary::id).toList();
    }
}
