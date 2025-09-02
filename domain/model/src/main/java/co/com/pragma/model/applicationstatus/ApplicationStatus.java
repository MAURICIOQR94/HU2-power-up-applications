package co.com.pragma.model.applicationstatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ApplicationStatus {

    private Long id;
    private String name;
    private String description;

}
