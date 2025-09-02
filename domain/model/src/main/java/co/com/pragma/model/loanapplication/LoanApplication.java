package co.com.pragma.model.loanapplication;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.loantype.LoanType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class LoanApplication {

    private UUID id;
    private UUID idUser;
    private String documentNumber;
    private BigDecimal amount;
    private Integer term;
    private LoanType loanType;
    private ApplicationStatus status;
    private LocalDateTime createdAt;

}
