package co.com.pragma.domain.gateways.sqs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QueueType {

    NOTIFICATIONS("notifications"),
    REPORTS("reports");

    private final String queueName;

}
