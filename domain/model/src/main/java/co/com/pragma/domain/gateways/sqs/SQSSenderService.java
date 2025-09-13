package co.com.pragma.domain.gateways.sqs;

import reactor.core.publisher.Mono;

public interface SQSSenderService {

    <T> Mono<String> send(T payload);

}
