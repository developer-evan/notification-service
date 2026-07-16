package com.mogeni.notificationserviceapplication.dto;


import com.mogeni.notificationserviceapplication.entity.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String id;
    private String recipient;
    private String subject;
    private NotificationStatus status;
    private Instant createdAt;
}
