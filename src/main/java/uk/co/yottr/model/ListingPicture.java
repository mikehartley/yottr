package uk.co.yottr.model;

import jakarta.persistence.*;

/**
 * A picture on a listing, kept as a JPEG scaled down for the listing's page, and as a thumbnail for the cards.
 * The pages load only the IDs and sizes (ListingPictureRepository.PictureSummary), never the pictures themselves.
 */
@Entity
@Table(name = "listing_pictures")
public class ListingPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boat_id", nullable = false)
    private Boat boat;

    // the first picture is the one with the lowest position
    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "width", nullable = false)
    private int width;

    @Column(name = "height", nullable = false)
    private int height;

    @Column(name = "image", nullable = false)
    private byte[] image;

    @Column(name = "thumbnail_width", nullable = false)
    private int thumbnailWidth;

    @Column(name = "thumbnail_height", nullable = false)
    private int thumbnailHeight;

    @Column(name = "thumbnail", nullable = false)
    private byte[] thumbnail;

    /**
     * For Hibernate.
     */
    protected ListingPicture() {
    }

    public ListingPicture(Boat boat, int position, Picture picture) {
        this.boat = boat;
        this.position = position;
        this.width = picture.image().width();
        this.height = picture.image().height();
        this.image = picture.image().jpeg();
        this.thumbnailWidth = picture.thumbnail().width();
        this.thumbnailHeight = picture.thumbnail().height();
        this.thumbnail = picture.thumbnail().jpeg();
    }

    public Long getId() {
        return id;
    }

    public Boat getBoat() {
        return boat;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * A picture ready to keep: the JPEG for the listing's page, and the thumbnail.
     */
    public record Picture(Jpeg image, Jpeg thumbnail) {
    }

    public record Jpeg(byte[] jpeg, int width, int height) {
    }
}
