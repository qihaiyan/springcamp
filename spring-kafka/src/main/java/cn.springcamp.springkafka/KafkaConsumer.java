package cn.springcamp.springkafka;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
public class KafkaConsumer {

    private String payload;

    @KafkaListener(topics = "test-topic")
    public void receive(ConsumerRecord<String, String> consumerRecord) {
        this.payload = consumerRecord.value();
        log.info("received payload='{}'", payload);
    }
}
