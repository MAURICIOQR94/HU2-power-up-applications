package co.com.pragma.domain.gateways.sqs;

import reactor.core.publisher.Mono;

public interface QueueSenderService {

    <T> Mono<String> send(QueueType queueType, T payload);

}
