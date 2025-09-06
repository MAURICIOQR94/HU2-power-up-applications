package co.com.pragma.r2dbc.entity;


import co.com.pragma.model.loantype.LoanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("loan_application")
public class LoanApplicationEntity {

    @Id
    private UUID id;

    @Column("id_user")
    private UUID idUser;

    @Column("document_number")
    private String documentNumber;

    private BigDecimal amount;

    private Integer term;

    @Column("id_status")
    private Long idStatus;

    @Column("id_loan_type")
    private Long idLoanType;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Transient
    private LoanType loanType;
}
