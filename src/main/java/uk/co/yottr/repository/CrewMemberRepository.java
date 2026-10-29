package uk.co.yottr.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.CrewMember;

import java.util.List;
import java.util.Optional;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {

    @EntityGraph(attributePaths = "member")
    List<CrewMember> findByBoatOrderByAskedOnAscIdAsc(Boat boat);

    Optional<CrewMember> findByBoatAndMemberUsername(Boat boat, String username);

    Optional<CrewMember> findByIdAndBoat(Long id, Boat boat);

    @EntityGraph(attributePaths = {"boat", "boat.owner"})
    List<CrewMember> findByMemberUsernameOrderByIdDesc(String username);
}
