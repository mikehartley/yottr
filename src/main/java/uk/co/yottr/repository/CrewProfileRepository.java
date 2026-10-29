package uk.co.yottr.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uk.co.yottr.model.CrewProfile;

import java.util.Optional;

public interface CrewProfileRepository extends JpaRepository<CrewProfile, Long>, JpaSpecificationExecutor<CrewProfile> {

    @EntityGraph(attributePaths = {"member", "availability"})
    Optional<CrewProfile> findByMemberUsername(String username);
}
