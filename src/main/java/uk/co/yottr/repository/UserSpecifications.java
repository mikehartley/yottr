package uk.co.yottr.repository;

import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;
import uk.co.yottr.form.MemberSearch;
import uk.co.yottr.model.User;

/**
 * The member queries, as specifications that a search combines.
 */
public final class UserSpecifications {

    private UserSpecifications() {
    }

    /**
     * The members that match every criterion given.
     */
    public static Specification<User> matching(MemberSearch search) {
        Specification<User> specification = Specification.unrestricted();

        if (search.getText() != null && !search.getText().isBlank()) {
            specification = specification.and(textMatches(search.getText().trim()));
        }
        if (search.getJoinedFrom() != null) {
            specification = specification.and((user, query, cb) ->
                    cb.greaterThanOrEqualTo(user.get("joined"), search.getJoinedFrom()));
        }
        if (search.getJoinedTo() != null) {
            specification = specification.and((user, query, cb) ->
                    cb.lessThanOrEqualTo(user.get("joined"), search.getJoinedTo()));
        }
        return specification;
    }

    /**
     * The text anywhere in the username, the full name or the email, ignoring case.
     */
    private static Specification<User> textMatches(String text) {
        final String pattern = Like.containing(text);
        return (user, query, cb) -> {
            final Expression<String> name = cb.concat(cb.concat(user.get("firstName"), " "), user.get("lastName"));
            return cb.or(
                    cb.like(cb.lower(user.get("username")), pattern, Like.ESCAPE),
                    cb.like(cb.lower(name), pattern, Like.ESCAPE),
                    cb.like(cb.lower(user.get("email")), pattern, Like.ESCAPE));
        };
    }
}
