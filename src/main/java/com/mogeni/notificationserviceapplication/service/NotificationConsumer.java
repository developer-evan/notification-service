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

        // Step 5 will replace this block with actual SMTP sending via JavaMailSender.
        log.info("Simulating email send to {} — subject: '{}'",
                notification.getRecipient(), notification.getSubject());

        notification.setStatus(NotificationStatus.SENT);
        notificationRepository.save(notification);

        log.info("Notification {} marked as SENT", notification.getId());
    }
}