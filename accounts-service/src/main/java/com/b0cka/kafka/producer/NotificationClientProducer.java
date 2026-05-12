package com.b0cka.kafka.producer;

import com.b0cka.events.ProfileUpdatedEvent;

public interface NotificationClientProducer {

    void sendToNotificationService(ProfileUpdatedEvent profileUpdatedEvent);

}
