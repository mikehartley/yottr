package uk.co.yottr.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.yottr.model.FinancialArrangement;

public interface FinancialArrangementRepository extends CrudRepository<FinancialArrangement, String> {
}
