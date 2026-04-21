package com.sales.bo.service;

import com.sales.bo.dto.SaleMessage;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SalePublisherService {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;

    public SalePublisherService(
        RabbitTemplate rabbitTemplate,
        @Value("${rabbitmq.exchange}") String exchangeName,
        @Value("${rabbitmq.routing-key}") String routingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public void publishEvent(SaleMessage saleMessage) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, saleMessage, message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }
}
