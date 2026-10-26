package uk.co.yottr.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.yottr.model.Frequency;

public interface FrequencyRepository extends CrudRepository<Frequency, String> {
}
