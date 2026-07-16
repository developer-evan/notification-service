package com.mogeni.notificationserviceapplication.config;


import com.mogeni.notificationserviceapplication.entity.Notification;
import com.mogeni.notificationserviceapplication.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoTestRunner implements CommandLineRunner {

    private final NotificationRepository notificationRepository;

    @Override
    public void run(String... args) {
        // This runner is intended as a lightweight connectivity check but must not
        // fail application startup if MongoDB is not available (e.g. developer machine
        // without a running mongo instance). Wrap the test in a try/catch and log
        // errors instead of letting exceptions propagate.
        try {
            Notification test = Notification.builder()
                    .recipient("test@example.com")
                    .subject("Mongo connectivity check")
                    .body("If you can read this in the logs, Mongo is connected.")
                    .build();

            Notification saved = notificationRepository.save(test);
            log.info(">>> Saved notification with id: {}", saved.getId());

            notificationRepository.findById(saved.getId())
                    .ifPresentOrElse(
                            n -> log.info(">>> Successfully read back: {}", n),
                            () -> log.error(">>> Could not read back the saved notification!")
                    );

            log.info(">>> Total notifications in collection: {}", notificationRepository.count());
        } catch (Exception ex) {
            // Log the error at debug/info level with the cause, but do not fail startup.
            log.warn(">>> MongoDB connectivity check failed - continuing without blocking application startup. Reason: {}", ex.getMessage());
            log.debug("MongoDB connectivity exception:", ex);
        }
    }
}
