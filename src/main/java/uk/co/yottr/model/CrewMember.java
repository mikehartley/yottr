package uk.co.yottr.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * A member who sails on a boat, or has asked to. They ask from the listing's page, and are in its crew once the
 * owner accepts; then the boat is among the ones they crew on, and the crew can see each other.
 */
@Entity
@Table(name = "boat_crew")
public class CrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boat_id")
    private Boat boat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User member;

    // false while they've asked and the owner hasn't yet accepted
    @Column(name = "on_board", nullable = false)
    private boolean onBoard;

    @Column(name = "asked_on", nullable = false)
    private LocalDate askedOn;

    @Column(name = "joined_on")
    private LocalDate joinedOn;

    /**
     * For Hibernate.
     */
    protected CrewMember() {
    }

    public CrewMember(Boat boat, User member, LocalDate askedOn) {
        this.boat = boat;
        this.member = member;
        this.askedOn = askedOn;
    }

    public void acceptOn(LocalDate today) {
        this.onBoard = true;
        this.joinedOn = today;
    }

    public Long getId() {
        return id;
    }

    public Boat getBoat() {
        return boat;
    }

    public User getMember() {
        return member;
    }

    public boolean isOnBoard() {
        return onBoard;
    }

    public LocalDate getAskedOn() {
        return askedOn;
    }

    public LocalDate getJoinedOn() {
        return joinedOn;
    }
}
