package uk.co.yottr.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * An IP address, or a range of them in CIDR notation, that an administrator has blocked from the site.
 */
@Entity
@Table(name = "blocked_addresses")
public class BlockedAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address", nullable = false, unique = true, length = 64)
    private String address;

    // why it was blocked, for the next administrator
    @Column(name = "note", length = 200)
    private String note;

    @Column(name = "blocked_on", nullable = false)
    private LocalDate blockedOn;

    /**
     * For Hibernate.
     */
    protected BlockedAddress() {
    }

    public BlockedAddress(String address, String note, LocalDate blockedOn) {
        this.address = address;
        this.note = note;
        this.blockedOn = blockedOn;
    }

    public Long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getNote() {
        return note;
    }

    public LocalDate getBlockedOn() {
        return blockedOn;
    }
}
