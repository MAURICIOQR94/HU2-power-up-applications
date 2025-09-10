package co.com.pragma.model.loanapplication;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.user.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class LoanApplication {

    private UUID id;
    private UUID idUser;
    private String email;
    private String documentNumber;
    private Double amount;
    private Integer term;
    private User user;
    private LoanType loanType;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private Double monthlyPayment;

}
