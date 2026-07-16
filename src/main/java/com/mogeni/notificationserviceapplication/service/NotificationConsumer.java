package com.mogeni.notificationserviceapplication.service;


import com.mogeni.notificationserviceapplication.dto.NotificationMessage;
import com.mogeni.notificationserviceapplication.entity.Notification;
import com.mogeni.notificationserviceapplication.entity.NotificationStatus;
import com.mogeni.notificationserviceapplication.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @RabbitListener(queues = "${notification.rabbitmq.queue}")
    public void handleNotification(NotificationMessage message) {
        log.info("Received notification message for id: {}", message.getNotificationId());

        Notification notification = notificationRepository.findById(message.getNotificationId())
                .orElse(null);

        if (notification == null) {
            log.warn("Notification {} not found — skipping (message will be discarded)",
                    message.getNotificationId());
            return;
        }

        try {
            emailService.sendEmail(
                    notification.getRecipient(),
                    notification.getSubject(),
                    notification.getBody()
            );

            notification.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notification);
            log.info("Notification {} marked as SENT", notification.getId());

        } catch (Exception e) {
            log.error("Failed to send notification {}: {}", notification.getId(), e.getMessage());

            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
            notificationRepository.save(notification);

            // Rethrow so RabbitMQ's retry/dead-letter mechanism still fires.
            // Without this, the message would be ack'd and silently lost on failure.
            throw e;
        }
    }
}