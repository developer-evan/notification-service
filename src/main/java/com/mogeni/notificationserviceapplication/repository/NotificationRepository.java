package com.mogeni.notificationserviceapplication.repository;


import com.mogeni.notificationserviceapplication.entity.Notification;
import com.mogeni.notificationserviceapplication.entity.NotificationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByRecipient(String recipient);
}