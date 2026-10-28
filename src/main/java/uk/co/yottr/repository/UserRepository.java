package uk.co.yottr.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import uk.co.yottr.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, CrudRepository<User, Long>,
        JpaSpecificationExecutor<User> {
    User findByUsername(String username);

    /**
     * Whether the username is taken, ignoring case: "Mike" and "mike" would be too easily mistaken for each other.
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * The user, with their row locked until the transaction ends.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.username = :username")
    User findByUsernameForUpdate(@Param("username") String username);
}
