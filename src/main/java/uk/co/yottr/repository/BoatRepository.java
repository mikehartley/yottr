package uk.co.yottr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface BoatRepository extends PagingAndSortingRepository<Boat, Long>, CrudRepository<Boat, Long>,
        JpaSpecificationExecutor<Boat> {
    Boat findByReference(String reference);

    /**
     * The listing with its owner loaded, for the listing's own page, which shows the owner's details. It is a LOAD
     * graph: the default, FETCH, would make everything else lazy, including the eager frequencies.
     */
    @EntityGraph(attributePaths = "owner", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Boat> findWithOwnerByReference(String reference);

    Optional<Boat> findByReferenceAndOwnerUsername(String reference, String ownerUsername);

    /**
     * The listing, locked until the transaction ends, so that two changes to its pictures at once can't both pass
     * the limit.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Boat b where b.reference = :reference")
    Boat findByReferenceForUpdate(@Param("reference") String reference);
    List<Boat> findByOwnerUsernameOrderByReference(String ownerUsername);
    long countByOwner(User owner);
}
