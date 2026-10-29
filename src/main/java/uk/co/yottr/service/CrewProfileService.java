package uk.co.yottr.service;

import jakarta.persistence.criteria.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.form.CrewProfileForm;
import uk.co.yottr.form.CrewSearch;
import uk.co.yottr.model.CrewProfile;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.CrewProfileRepository;
import uk.co.yottr.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Crew profiles, and owners finding crew by them.
 */
@Service
public class CrewProfileService {

    // the most recently updated first, up to this many
    public static final int SEARCH_LIMIT = 100;

    private CrewProfileRepository crewProfileRepository;
    private UserRepository userRepository;
    private TemporalProvider temporalProvider;

    @Autowired
    public CrewProfileService(CrewProfileRepository crewProfileRepository, UserRepository userRepository, TemporalProvider temporalProvider) {
        this.crewProfileRepository = crewProfileRepository;
        this.userRepository = userRepository;
        this.temporalProvider = temporalProvider;
    }

    @Transactional(readOnly = true)
    public Optional<CrewProfile> profileOf(String username) {
        return crewProfileRepository.findByMemberUsername(username);
    }

    /**
     * Makes or changes the member's profile from the form.
     */
    @Transactional
    public CrewProfile save(String username, CrewProfileForm form) {
        final CrewProfile profile = crewProfileRepository.findByMemberUsername(username)
                .orElseGet(() -> new CrewProfile(userRepository.findByUsername(username)));
        form.copyTo(profile);
        profile.setLastUpdated(temporalProvider.today());
        return crewProfileRepository.save(profile);
    }

    /**
     * Removes the member's profile, so owners no longer find them.
     */
    @Transactional
    public void remove(String username) {
        crewProfileRepository.findByMemberUsername(username).ifPresent(crewProfileRepository::delete);
    }

    /**
     * The profiles of enabled members, on Yottr as crew, that match the search, with their members. A member who
     * stops being crew keeps their profile, unseen, for if they start again.
     */
    @Transactional(readOnly = true)
    public List<CrewProfile> search(CrewSearch search) {
        Specification<CrewProfile> specification = (profile, query, cb) -> {
            final Join<CrewProfile, User> member = profile.join("member");
            return cb.and(cb.isTrue(member.get("enabled")), cb.isTrue(member.get("crew")));
        };
        if (search.getRegion() != null) {
            specification = specification.and((profile, query, cb) -> cb.equal(profile.get("region"), search.getRegion()));
        }
        if (search.getMinLevel() != null) {
            specification = specification.and((profile, query, cb) -> cb.ge(profile.get("levelRank"), search.getMinLevel()));
        }
        if (search.getWhen() != null) {
            specification = specification.and((profile, query, cb) -> cb.isMember(search.getWhen(), profile.get("availability")));
        }
        final List<CrewProfile> profiles = crewProfileRepository.findAll(specification,
                PageRequest.of(0, SEARCH_LIMIT, Sort.by(Sort.Direction.DESC, "lastUpdated", "id"))).getContent();
        profiles.forEach(profile -> profile.getMember().getEmail());
        return profiles;
    }
}
