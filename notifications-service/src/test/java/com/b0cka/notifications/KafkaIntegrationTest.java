package com.b0cka.notifications;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor
@EmbeddedKafka(partitions = 1, topics = {"bank.events"})
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest"
})
class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void shouldSendAndReceiveEvent_withAtLeastOnceStrategy() throws Exception {
        kafkaTemplate.send("bank.events", "user-123", "test-notification").get();

        Map<String, Object> props = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (var consumer = new DefaultKafkaConsumerFactory<String, String>(
                props, new StringDeserializer(), new StringDeserializer()).createConsumer()) {

            consumer.subscribe(Collections.singletonList("bank.events"));

            var record = KafkaTestUtils.getSingleRecord(consumer, "bank.events", Duration.ofSeconds(5));

            assertThat(record.key()).isEqualTo("user-123");
            assertThat(record.value()).isEqualTo("test-notification");
        }
    }
}