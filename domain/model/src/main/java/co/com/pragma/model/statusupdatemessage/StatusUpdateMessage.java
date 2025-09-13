package co.com.pragma.model.statusupdatemessage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class StatusUpdateMessage {

    private String email;
    private String name;
    private String status;

}
