package co.com.pragma.sqs.sender;

import co.com.pragma.domain.gateways.sqs.QueueSenderService;
import co.com.pragma.domain.gateways.sqs.QueueType;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class QueueSenderServiceImpl implements QueueSenderService {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public <T> Mono<String> send(QueueType queueType, T payload) {
        String queueUrl = getQueueUrl(queueType);
        return Mono.fromCallable(() -> serialize(payload))
                .map(messageBody -> buildRequest(queueUrl, messageBody))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String queueUrl, String message) {
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing message to JSON", e);
        }
    }

    private String getQueueUrl(QueueType queueType) {
        String queueUrl = properties.queues().get(queueType.getQueueName());
        if (queueUrl == null) {
            throw new IllegalArgumentException(
                    "Queue URL not configured for: " + queueType.getQueueName());
        }
        return queueUrl;
    }
}
