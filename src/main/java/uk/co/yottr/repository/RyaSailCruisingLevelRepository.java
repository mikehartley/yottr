package uk.co.yottr.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.yottr.model.RyaSailCruisingLevel;

public interface RyaSailCruisingLevelRepository extends CrudRepository<RyaSailCruisingLevel, Long> {
    RyaSailCruisingLevel findByRank(int rank);
}
