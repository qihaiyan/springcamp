package cn.springcamp.springkafka;

import cn.springcamp.springkafka.container.MessageListenerContainerConsumer;
import cn.springcamp.springkafka.listener.KafkaListenerConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.Map;

import static cn.springcamp.springkafka.container.MessageListenerContainerConsumer.LISTENER_CONTAINER_TOPIC;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(
        count = 1,
        controlledShutdown = true,
        topics = { "test-topic", "testEmbeddedIn", "testEmbeddedOut", "container-topic" }
)
class ApplicationTest {

    private static final String CLOUD_STREAM_INPUT_TOPIC = "testEmbeddedIn";
    private static final String CLOUD_STREAM_OUTPUT_TOPIC = "testEmbeddedOut";
    private static final String KAFKA_LISTENER_TOPIC = "test-topic";
    private static final String GROUP_NAME = "embeddedKafkaApplication";

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        // @EmbeddedKafka auto-sets spring.kafka.bootstrap-servers; point the cloud-stream binder at the same broker.
        registry.add("spring.cloud.stream.kafka.binder.brokers", () -> System.getProperty(EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS));
    }

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private KafkaListenerConsumer kafkaListenerConsumer;
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private MessageListenerContainerConsumer messageListenerContainerConsumer;

    private KafkaTemplate<String, Object> kafkaTemplate;

    private Consumer<String, Object> consumer;

    @BeforeEach
    void setup() {
        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
        DefaultKafkaProducerFactory<String, Object> pf = new DefaultKafkaProducerFactory<>(senderProps);
        kafkaTemplate = new KafkaTemplate<>(pf, true);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafka);
        DefaultKafkaConsumerFactory<String, Object> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = cf.createConsumer();
        embeddedKafka.consumeFromEmbeddedTopics(consumer, KAFKA_LISTENER_TOPIC, CLOUD_STREAM_OUTPUT_TOPIC);
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void testKafkaLisener() {
        kafkaProducer.send(KAFKA_LISTENER_TOPIC, "foo");
        await().until(() -> "foo".equals(kafkaListenerConsumer.getPayload()));
    }

    @Test
    void testListenerContainer() {
        kafkaTemplate.send(LISTENER_CONTAINER_TOPIC, "foo");
        await().until(() -> messageListenerContainerConsumer.consumedMessages.contains("foo"));
    }

    @Test
    void testCloudStream() {
        kafkaTemplate.send(CLOUD_STREAM_INPUT_TOPIC, "foo");

        ConsumerRecord<String, Object> cr = KafkaTestUtils.getSingleRecord(consumer, CLOUD_STREAM_OUTPUT_TOPIC, Duration.ofMillis(3000));

        System.out.println("ConsumerRecord : " + cr.value());
        assertThat(cr.value(), is("FOO"));
    }
}
