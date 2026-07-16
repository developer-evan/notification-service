package com.mogeni.notificationserviceapplication.service;


import com.mogeni.notificationserviceapplication.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${notification.rabbitmq.exchange}")
    private String exchange;

    @Value("${notification.rabbitmq.routing-key}")
    private String routingKey;

    public void publish(String notificationId) {
        NotificationMessage message = new NotificationMessage(notificationId);
        log.info("Publishing notification {} to exchange '{}'", notificationId, exchange);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
