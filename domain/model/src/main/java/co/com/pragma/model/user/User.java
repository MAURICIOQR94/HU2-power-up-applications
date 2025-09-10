package co.com.pragma.model.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class User {

    private String email;
    private String firstName;
    private String lastName;
    private Double baseSalary;

}
