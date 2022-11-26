package cn.springcamp.springkafka.container;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Component
public class MessageListenerContainerConsumer {

    public static final String LISTENER_CONTAINER_TOPIC = "container-topic";

    public Set<String> consumedMessages = new HashSet<>();

    @PostConstruct
    void start() {
        MessageListener<String, String> messageListener = record -> {
            System.out.println("MessageListenerContainerConsumer received message: " + record.value());
            consumedMessages.add(record.value());
        };

        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(
                        consumerFactory(),
                        containerProperties(LISTENER_CONTAINER_TOPIC, messageListener));

        container.start();
    }

    private DefaultKafkaConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                new HashMap<String, Object>() {
                    {
                        put(BOOTSTRAP_SERVERS_CONFIG, System.getProperty("spring.kafka.bootstrap-servers"));
                        put(GROUP_ID_CONFIG, "groupId");
                        put(AUTO_OFFSET_RESET_CONFIG, "earliest");
                    }
                },
                new StringDeserializer(),
                new StringDeserializer());
    }

    private ContainerProperties containerProperties(String topic, MessageListener<String, String> messageListener) {
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setMessageListener(messageListener);
        return containerProperties;
    }
}
