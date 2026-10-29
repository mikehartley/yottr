package uk.co.yottr.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import uk.co.yottr.form.BoatSearch;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.Frequency;
import uk.co.yottr.model.User;

import java.time.LocalDate;

/**
 * The listing queries, as specifications that a search combines.
 */
public final class BoatSpecifications {

    private static final double FEET_PER_METRE = 3.28084;

    private BoatSpecifications() {
    }

    /**
     * Listings other members may see: not suspended, not withdrawn, and their owner's account enabled.
     * Boat.isVisible is the same rule for a single listing.
     */
    public static Specification<Boat> visible() {
        return (boat, query, cb) -> {
            final Join<Boat, User> owner = boat.join("owner");
            return cb.and(cb.isFalse(boat.get("suspended")), cb.isFalse(boat.get("withdrawn")), cb.isTrue(owner.get("enabled")));
        };
    }

    /**
     * The visible listings that match every criterion given, with how recently posted counted back from today.
     */
    public static Specification<Boat> matching(BoatSearch search, LocalDate today) {
        Specification<Boat> specification = visible();

        if (search.getText() != null && !search.getText().isBlank()) {
            specification = specification.and(textMatches(search.getText().trim()));
        }
        if (search.getRegion() != null) {
            specification = specification.and((boat, query, cb) ->
                    cb.or(cb.equal(boat.get("fromRegion"), search.getRegion()), cb.equal(boat.get("toRegion"), search.getRegion())));
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
        if (search.getPostedWithin() != null) {
            final LocalDate since = today.minusDays(search.getPostedWithin());
            specification = specification.and((boat, query, cb) -> cb.greaterThanOrEqualTo(boat.get("firstPosted"), since));
        }
        // A listing's dates may be open at either end: no start is from now, no end is open-ended.
        if (search.getSailingFrom() != null) {
            specification = specification.and((boat, query, cb) ->
                    cb.or(cb.isNull(boat.get("whenTo")), cb.greaterThanOrEqualTo(boat.get("whenTo"), search.getSailingFrom())));
        }
        if (search.getSailingTo() != null) {
            specification = specification.and((boat, query, cb) ->
                    cb.or(cb.isNull(boat.get("whenFrom")), cb.lessThanOrEqualTo(boat.get("whenFrom"), search.getSailingTo())));
        }
        if (search.getMinLength() != null) {
            specification = specification.and((boat, query, cb) ->
                    cb.ge(lengthInFeet(boat, cb), search.getMinLength()));
        }
        if (search.getMaxLength() != null) {
            specification = specification.and((boat, query, cb) ->
                    cb.le(lengthInFeet(boat, cb), search.getMaxLength()));
        }
        return specification;
    }

    /**
     * The listing's length in feet, converting it if it was given in metres.
     */
    private static Expression<Double> lengthInFeet(Root<Boat> boat, CriteriaBuilder cb) {
        final Expression<Double> length = boat.<Integer>get("length").as(Double.class);
        return cb.<Double>selectCase()
                .when(cb.isTrue(boat.get("isUnitsImperial")), length)
                .otherwise(cb.prod(length, FEET_PER_METRE));
    }

    /**
     * The text anywhere in the title, make and model, description or places, ignoring case, or the whole reference.
     */
    private static Specification<Boat> textMatches(String text) {
        final String pattern = Like.containing(text);
        return (boat, query, cb) -> cb.or(
                cb.like(cb.lower(boat.get("title")), pattern, Like.ESCAPE),
                cb.like(cb.lower(boat.get("makeAndModel")), pattern, Like.ESCAPE),
                cb.like(cb.lower(boat.get("description")), pattern, Like.ESCAPE),
                cb.like(cb.lower(boat.get("fromPlace")), pattern, Like.ESCAPE),
                cb.like(cb.lower(boat.get("toPlace")), pattern, Like.ESCAPE),
                cb.equal(boat.get("reference"), text));
    }
}
