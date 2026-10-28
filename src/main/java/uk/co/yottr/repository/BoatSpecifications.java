package uk.co.yottr.repository;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import uk.co.yottr.form.BoatSearch;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.Frequency;
import uk.co.yottr.model.User;

/**
 * The listing queries, as specifications that a search combines.
 */
public final class BoatSpecifications {

    private BoatSpecifications() {
    }

    /**
     * Listings other members may see: not suspended, and their owner's account enabled. Boat.isVisible is the
     * same rule for a single listing.
     */
    public static Specification<Boat> visible() {
        return (boat, query, cb) -> {
            final Join<Boat, User> owner = boat.join("owner");
            return cb.and(cb.isFalse(boat.get("suspended")), cb.isTrue(owner.get("enabled")));
        };
    }

    /**
     * The visible listings that match every criterion given.
     */
    public static Specification<Boat> matching(BoatSearch search) {
        Specification<Boat> specification = visible();

        if (search.getText() != null && !search.getText().isBlank()) {
            specification = specification.and(textMatches(search.getText().trim()));
        }
        if (search.getPurpose() != null) {
            specification = specification.and((boat, query, cb) -> cb.equal(boat.get("sailingPurpose"), search.getPurpose()));
        }
        if (search.getWhen() != null) {
            specification = specification.and((boat, query, cb) -> {
                // a listing can have several frequencies, so the join could repeat it
                query.distinct(true);
                final Join<Boat, Frequency> frequency = boat.join("frequency");
                return cb.equal(frequency.get("name"), search.getWhen().name());
            });
        }
        if (search.getCosts() != null) {
            specification = specification.and((boat, query, cb) ->
                    cb.equal(boat.get("financialArrangement").get("name"), search.getCosts().name()));
        }
        if (search.getMyLevel() != null) {
            specification = specification.and((boat, query, cb) ->
                    cb.lessThanOrEqualTo(boat.get("minimumRequiredLevel").get("rank"), search.getMyLevel()));
        }
        return specification;
    }

    /**
     * The text anywhere in the title, make and model or description, ignoring case, or the whole reference.
     */
    private static Specification<Boat> textMatches(String text) {
        final String pattern = Like.containing(text);
        return (boat, query, cb) -> cb.or(
                cb.like(cb.lower(boat.get("title")), pattern, Like.ESCAPE),
                cb.like(cb.lower(boat.get("makeAndModel")), pattern, Like.ESCAPE),
                cb.like(cb.lower(boat.get("description")), pattern, Like.ESCAPE),
                cb.equal(boat.get("reference"), text));
    }
}
