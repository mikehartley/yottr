package uk.co.yottr.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "financial_arrangement")
public class FinancialArrangement {

    // Worded as what happens to the crew, which reads the same to an owner posting a listing and to crew searching.
    // V7 changed the stored names to match.
    public enum FinancialArrangementEnum {
        NO_CONTRIBUTION("No cost to crew"),
        FOOD_ONLY("Crew share food only"),
        SHARED_FIXED("Crew pay a fixed share"),
        SHARED_VARIABLE("Crew share running costs"),
        COMMERCIAL("Crew pay (charter, trip or course)"),
        PAID("Crew are paid");

        private String displayName;

        private FinancialArrangementEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static FinancialArrangement NO_CONTRIBUTION =  new FinancialArrangement(FinancialArrangementEnum.NO_CONTRIBUTION);
    public static FinancialArrangement FOOD_ONLY =  new FinancialArrangement(FinancialArrangementEnum.FOOD_ONLY);
    public static FinancialArrangement SHARED_FIXED =  new FinancialArrangement(FinancialArrangementEnum.SHARED_FIXED);
    public static FinancialArrangement SHARED_VARIABLE =  new FinancialArrangement(FinancialArrangementEnum.SHARED_VARIABLE);
    public static FinancialArrangement COMMERCIAL =  new FinancialArrangement(FinancialArrangementEnum.COMMERCIAL);
    public static FinancialArrangement PAID =  new FinancialArrangement(FinancialArrangementEnum.PAID);

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "display_name")
    private String displayName;

    protected FinancialArrangement() {
        // for JPA, which needs it to be at least protected so that Hibernate can make proxies
    }

    public FinancialArrangement(FinancialArrangementEnum financialArrangementValue) {
        if (financialArrangementValue == null) {
            throw new IllegalArgumentException("cannot construct me with null argument");
        }

        this.name = financialArrangementValue.name();
        this.displayName = financialArrangementValue.getDisplayName();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * The name, which is how a form writes this as an option and binds it back to its enum.
     */
    @Override
    public String toString() {
        return getName();
    }
}
