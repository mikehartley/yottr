package uk.co.yottr.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.ListingPicture;

import java.util.List;
import java.util.Optional;

public interface ListingPictureRepository extends CrudRepository<ListingPicture, Long> {

    /**
     * What a page needs to show a picture, without the picture itself.
     */
    record PictureSummary(Long id, int width, int height, int thumbnailWidth, int thumbnailHeight) {
    }

    @Query("select new uk.co.yottr.repository.ListingPictureRepository$PictureSummary(p.id, p.width, p.height, p.thumbnailWidth, p.thumbnailHeight)"
            + " from ListingPicture p where p.boat = :boat order by p.position, p.id")
    List<PictureSummary> findSummaries(@Param("boat") Boat boat);

    long countByBoat(Boat boat);

    @Query("select min(p.position) from ListingPicture p where p.boat = :boat")
    Optional<Integer> findFirstPosition(@Param("boat") Boat boat);

    @Query("select max(p.position) from ListingPicture p where p.boat = :boat")
    Optional<Integer> findLastPosition(@Param("boat") Boat boat);

    Optional<ListingPicture> findByIdAndBoat(Long id, Boat boat);

    @Query("select p.image from ListingPicture p where p.id = :id and p.boat = :boat")
    Optional<byte[]> findImage(@Param("id") Long id, @Param("boat") Boat boat);

    @Query("select p.thumbnail from ListingPicture p where p.id = :id and p.boat = :boat")
    Optional<byte[]> findThumbnail(@Param("id") Long id, @Param("boat") Boat boat);

    @Modifying
    @Query("delete from ListingPicture p where p.id = :id and p.boat = :boat")
    int deleteByIdAndBoat(@Param("id") Long id, @Param("boat") Boat boat);
}
