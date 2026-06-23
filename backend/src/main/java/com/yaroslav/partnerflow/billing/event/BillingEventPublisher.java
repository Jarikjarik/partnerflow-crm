package com.yaroslav.partnerflow.billing.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaroslav.partnerflow.billing.dto.DealWonMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class BillingEventPublisher {

    public static final String DEAL_WON_QUEUE = "partnerflow.deal.won";

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void publishDealWon(Long dealId, Long actorId) {
        DealWonMessage message = new DealWonMessage(
                dealId,
                actorId,
                Instant.now()
        );

        Runnable publishAction = () -> {
            try {
                String payload = objectMapper.writeValueAsString(message);
                jmsTemplate.convertAndSend(DEAL_WON_QUEUE, payload);
            } catch (JsonProcessingException ex) {
                throw new IllegalStateException("Failed to serialize deal won event", ex);
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishAction.run();
                }
            });
        } else {
            publishAction.run();
        }
    }
}