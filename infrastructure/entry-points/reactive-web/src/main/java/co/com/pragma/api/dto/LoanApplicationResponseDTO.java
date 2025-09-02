package co.com.pragma.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoanApplicationResponseDTO {


    private String documentNumber;
    private LocalDateTime createdAt;

}
