package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.exception.ResourceNotFoundException;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.CrewMember;
import uk.co.yottr.repository.CrewMemberRepository;
import uk.co.yottr.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * The crews of boats (CrewMember). The callers check who may do what: any member who can see a listing may ask to
 * join its crew, and only its owner may accept or remove them.
 */
@Service
public class CrewService {

    private CrewMemberRepository crewMemberRepository;
    private UserRepository userRepository;
    private TemporalProvider temporalProvider;

    @Autowired
    public CrewService(CrewMemberRepository crewMemberRepository, UserRepository userRepository, TemporalProvider temporalProvider) {
        this.crewMemberRepository = crewMemberRepository;
        this.userRepository = userRepository;
        this.temporalProvider = temporalProvider;
    }

    /**
     * Everyone in the boat's crew or asking to be, in the order they asked, with their details.
     */
    @Transactional(readOnly = true)
    public List<CrewMember> crewOf(Boat boat) {
        return crewMemberRepository.findByBoatOrderByAskedOnAscIdAsc(boat);
    }

    /**
     * The member's place in the boat's crew, if they're in it or have asked.
     */
    @Transactional(readOnly = true)
    public Optional<CrewMember> placeIn(Boat boat, String username) {
        return crewMemberRepository.findByBoatAndMemberUsername(boat, username);
    }

    /**
     * Asks for the member to join the boat's crew. Asking again changes nothing.
     *
     * @throws IllegalOperationException if they own the boat
     */
    @Transactional
    public void ask(Boat boat, String username) {
        if (boat.getOwner().getUsername().equals(username)) {
            throw new IllegalOperationException("An owner can't join their own crew.");
        }
        if (placeIn(boat, username).isEmpty()) {
            crewMemberRepository.save(new CrewMember(boat, userRepository.findByUsername(username), temporalProvider.today()));
        }
    }

    /**
     * Takes the member out of the boat's crew, or withdraws their asking.
     */
    @Transactional
    public void leave(Boat boat, String username) {
        placeIn(boat, username).ifPresent(crewMemberRepository::delete);
    }

    @Transactional
    public void accept(Boat boat, long crewMemberId) {
        find(boat, crewMemberId).acceptOn(temporalProvider.today());
    }

    /**
     * Turns down someone who asked, or takes someone out of the crew.
     */
    @Transactional
    public void remove(Boat boat, long crewMemberId) {
        crewMemberRepository.delete(find(boat, crewMemberId));
    }

    /**
     * The boats the member crews on or has asked to, the most recent first, with their owners.
     */
    @Transactional(readOnly = true)
    public List<CrewMember> crewing(String username) {
        return crewMemberRepository.findByMemberUsernameOrderByIdDesc(username);
    }

    private CrewMember find(Boat boat, long crewMemberId) {
        return crewMemberRepository.findByIdAndBoat(crewMemberId, boat)
                .orElseThrow(() -> new ResourceNotFoundException("No crew member " + crewMemberId + " on listing " + boat.getReference()));
    }
}
