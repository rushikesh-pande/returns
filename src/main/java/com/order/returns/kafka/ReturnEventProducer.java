package com.order.returns.kafka;

import com.order.returns.model.Return;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReturnEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String RETURN_INITIATED_TOPIC = "return.initiated";
    private static final String RETURN_APPROVED_TOPIC = "return.approved";
    private static final String RETURN_REJECTED_TOPIC = "return.rejected";
    private static final String REFUND_PROCESSED_TOPIC = "refund.processed";

    public void publishReturnInitiated(Return returnEntity) {
        try {
            String message = objectMapper.writeValueAsString(returnEntity);
            kafkaTemplate.send(RETURN_INITIATED_TOPIC, returnEntity.getReturnId(), message);
            log.info("Published return initiated event for return: {}", returnEntity.getReturnId());
        } catch (Exception e) {
            log.error("Error publishing return initiated event: {}", e.getMessage(), e);
        }
    }

    public void publishReturnApproved(Return returnEntity) {
        try {
            String message = objectMapper.writeValueAsString(returnEntity);
            kafkaTemplate.send(RETURN_APPROVED_TOPIC, returnEntity.getReturnId(), message);
            log.info("Published return approved event for return: {}", returnEntity.getReturnId());
        } catch (Exception e) {
            log.error("Error publishing return approved event: {}", e.getMessage(), e);
        }
    }

    public void publishReturnRejected(Return returnEntity) {
        try {
            String message = objectMapper.writeValueAsString(returnEntity);
            kafkaTemplate.send(RETURN_REJECTED_TOPIC, returnEntity.getReturnId(), message);
            log.info("Published return rejected event for return: {}", returnEntity.getReturnId());
        } catch (Exception e) {
            log.error("Error publishing return rejected event: {}", e.getMessage(), e);
        }
    }

    public void publishRefundProcessed(Return returnEntity) {
        try {
            String message = objectMapper.writeValueAsString(returnEntity);
            kafkaTemplate.send(REFUND_PROCESSED_TOPIC, returnEntity.getReturnId(), message);
            log.info("Published refund processed event for return: {}", returnEntity.getReturnId());
        } catch (Exception e) {
            log.error("Error publishing refund processed event: {}", e.getMessage(), e);
        }
    }
}

