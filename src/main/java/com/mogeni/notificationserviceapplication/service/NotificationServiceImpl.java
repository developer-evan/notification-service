package com.mogeni.notificationserviceapplication.service;


import com.mogeni.notificationserviceapplication.dto.NotificationRequest;
import com.mogeni.notificationserviceapplication.dto.NotificationResponse;
import com.mogeni.notificationserviceapplication.entity.Notification;
import com.mogeni.notificationserviceapplication.entity.NotificationStatus;
import com.mogeni.notificationserviceapplication.exception.NotificationNotFoundException;
import com.mogeni.notificationserviceapplication.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationProducer notificationProducer;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .body(request.getBody())
                .status(NotificationStatus.PENDING)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Saved notification {} with status PENDING", saved.getId());

        notificationProducer.publish(saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public NotificationResponse getNotificationById(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        return mapToResponse(notification);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipient(notification.getRecipient())
                .subject(notification.getSubject())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
