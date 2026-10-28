package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The listings members save to come back to.
 */
@Service
public class SavedBoatsService {

    private UserRepository userRepository;
    private BoatRepository boatRepository;

    @Autowired
    public SavedBoatsService(UserRepository userRepository, BoatRepository boatRepository) {
        this.userRepository = userRepository;
        this.boatRepository = boatRepository;
    }

    @Transactional
    public void save(String username, String reference) {
        final Boat boat = boatRepository.findByReference(reference);
        if (boat != null) {
            userRepository.findByUsername(username).getSavedBoats().add(boat);
        }
    }

    @Transactional
    public void unsave(String username, String reference) {
        userRepository.findByUsername(username).getSavedBoats().removeIf(boat -> boat.getReference().equals(reference));
    }

    /**
     * The member's saved listings, the most recently posted first, with their owners loaded. They include listings
     * other members can no longer see, which the page shows as no longer available.
     */
    @Transactional(readOnly = true)
    public List<Boat> savedBoats(String username) {
        final List<Boat> boats = userRepository.findByUsername(username).getSavedBoats().stream()
                .sorted(Comparator.comparing(Boat::getFirstPosted).reversed().thenComparing(Boat::getTitle))
                .toList();
        boats.forEach(boat -> boat.getOwner().isEnabled());
        return boats;
    }

    /**
     * The references of the member's saved listings, for showing which are saved.
     */
    @Transactional(readOnly = true)
    public Set<String> savedReferences(String username) {
        return userRepository.findByUsername(username).getSavedBoats().stream()
                .map(Boat::getReference)
                .collect(Collectors.toSet());
    }
}
