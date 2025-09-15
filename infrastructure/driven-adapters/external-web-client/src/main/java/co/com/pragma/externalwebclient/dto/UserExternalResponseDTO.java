package co.com.pragma.externalwebclient.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserExternalResponseDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private Double baseSalary;
}
