package co.com.pragma.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanReportMessage {
    private String status;
    private Double amount;
}
