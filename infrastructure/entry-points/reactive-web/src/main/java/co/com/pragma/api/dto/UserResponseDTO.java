package co.com.pragma.api.dto;

import lombok.Data;

@Data
public class UserResponseDTO {

    private String email;
    private String firstName;
    private String lastName;
    private Double baseSalary;

    public String getBaseSalary() {
        return baseSalary == null ? null : String.format("%,.2f", baseSalary);
    }

}
