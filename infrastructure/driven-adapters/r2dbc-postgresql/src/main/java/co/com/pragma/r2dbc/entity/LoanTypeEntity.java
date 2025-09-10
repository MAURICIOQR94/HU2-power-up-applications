package co.com.pragma.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("loan_type")
public class LoanTypeEntity {

    @Id
    private Long id;

    private String name;

    @Column("min_amount")
    private Double minAmount;

    @Column("max_amount")
    private Double maxAmount;

    @Column("interest_rate")
    private Float interestRate;

    @Column("automatic_validation")
    private boolean automaticValidation;
}
