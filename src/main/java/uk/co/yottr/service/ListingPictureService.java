package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.ListingPicture;
import uk.co.yottr.model.ListingPicture.Picture;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.ListingPictureRepository;
import uk.co.yottr.repository.ListingPictureRepository.PictureSummary;

import java.util.List;
import java.util.Optional;

/**
 * The pictures on listings. The callers check that the member may see or change the listing; this keeps each
 * picture to its own listing.
 */
@Service
public class ListingPictureService {

    public static final int MAX_PICTURES = 6;

    private BoatRepository boatRepository;
    private ListingPictureRepository listingPictureRepository;
    private TemporalProvider temporalProvider;

    @Autowired
    public ListingPictureService(BoatRepository boatRepository, ListingPictureRepository listingPictureRepository,
                                 TemporalProvider temporalProvider) {
        this.boatRepository = boatRepository;
        this.listingPictureRepository = listingPictureRepository;
        this.temporalProvider = temporalProvider;
    }

    /**
     * The listing's pictures, first to last.
     */
    @Transactional(readOnly = true)
    public List<PictureSummary> pictures(Boat boat) {
        return listingPictureRepository.findSummaries(boat);
    }

    /**
     * How many more pictures the listing can have.
     */
    @Transactional(readOnly = true)
    public int roomFor(Boat boat) {
        return (int) Math.max(0, MAX_PICTURES - listingPictureRepository.countByBoat(boat));
    }

    /**
     * Adds the pictures after the listing's others, as many as there's room for.
     *
     * @return how many were added
     */
    @Transactional
    public int add(Boat boat, List<Picture> pictures) {
        final Boat locked = boatRepository.findByReferenceForUpdate(boat.getReference());
        final List<Picture> adding = pictures.stream().limit(roomFor(locked)).toList();
        int position = listingPictureRepository.findLastPosition(locked).map(last -> last + 1).orElse(0);
        for (Picture picture : adding) {
            listingPictureRepository.save(new ListingPicture(locked, position++, picture));
        }
        if (!adding.isEmpty()) {
            locked.setLastUpdated(temporalProvider.today());
        }
        return adding.size();
    }

    /**
     * Makes the picture the listing's first, the one on its card.
     */
    @Transactional
    public void makeFirst(Boat boat, long pictureId) {
        final Boat locked = boatRepository.findByReferenceForUpdate(boat.getReference());
        final ListingPicture picture = listingPictureRepository.findByIdAndBoat(pictureId, locked)
                .orElseThrow(() -> notFound(pictureId, boat));
        final int first = listingPictureRepository.findFirstPosition(locked).orElseThrow();
        if (picture.getPosition() != first) {
            picture.setPosition(first - 1);
            locked.setLastUpdated(temporalProvider.today());
        }
    }

    /**
     * Removes a picture for the listing's owner, which marks the listing updated.
     */
    @Transactional
    public void remove(Boat boat, long pictureId) {
        final Boat locked = boatRepository.findByReferenceForUpdate(boat.getReference());
        delete(locked, pictureId);
        locked.setLastUpdated(temporalProvider.today());
    }

    /**
     * Removes a picture for an administrator. The listing isn't marked updated: its owner hasn't changed it.
     */
    @Transactional
    public void takeDown(Boat boat, long pictureId) {
        delete(boat, pictureId);
    }

    @Transactional(readOnly = true)
    public Optional<byte[]> image(Boat boat, long pictureId) {
        return listingPictureRepository.findImage(pictureId, boat);
    }

    @Transactional(readOnly = true)
    public Optional<byte[]> thumbnail(Boat boat, long pictureId) {
        return listingPictureRepository.findThumbnail(pictureId, boat);
    }

    private void delete(Boat boat, long pictureId) {
        if (listingPictureRepository.deleteByIdAndBoat(pictureId, boat) == 0) {
            throw notFound(pictureId, boat);
        }
    }

    private static ResourceNotFoundException notFound(long pictureId, Boat boat) {
        return new ResourceNotFoundException("No picture " + pictureId + " on listing " + boat.getReference());
    }
}
