package com.yaroslav.partnerflow.billing.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaroslav.partnerflow.billing.dto.DealWonMessage;
import com.yaroslav.partnerflow.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillingEventListener {

    private final ObjectMapper objectMapper;
    private final BillingService billingService;

    @JmsListener(destination = BillingEventPublisher.DEAL_WON_QUEUE)
    public void onDealWon(String payload) {
        try {
            DealWonMessage message = objectMapper.readValue(payload, DealWonMessage.class);
            billingService.processDealWon(message, payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid deal won event payload", ex);
        }
    }
}