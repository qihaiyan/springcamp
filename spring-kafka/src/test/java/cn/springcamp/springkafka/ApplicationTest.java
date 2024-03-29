package cn.springcamp.springkafka;

import cn.springcamp.springkafka.container.MessageListenerContainerConsumer;
import cn.springcamp.springkafka.listener.KafkaListenerConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.Map;

import static cn.springcamp.springkafka.container.MessageListenerContainerConsumer.LISTENER_CONTAINER_TOPIC;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class ApplicationTest {

    private static final String CLOUD_STREAM_INPUT_TOPIC = "testEmbeddedIn";
    private static final String CLOUD_STREAM_OUTPUT_TOPIC = "testEmbeddedOut";
    private static final String KAFKA_LISTENER_TOPIC = "test-topic";
    private static final String GROUP_NAME = "embeddedKafkaApplication";

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, KAFKA_LISTENER_TOPIC, CLOUD_STREAM_INPUT_TOPIC, CLOUD_STREAM_OUTPUT_TOPIC);

    public static EmbeddedKafkaBroker embeddedKafka = embeddedKafkaRule.getEmbeddedKafka();

    private static KafkaTemplate<String, Object> kafkaTemplate;

    private static Consumer<String, Object> consumer;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private KafkaListenerConsumer kafkaListenerConsumer;
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private MessageListenerContainerConsumer messageListenerContainerConsumer;

    @BeforeClass
    public static void setup() {
        System.setProperty("spring.cloud.stream.kafka.binder.brokers", embeddedKafka.getBrokersAsString());
        System.setProperty("spring.kafka.bootstrap-servers", embeddedKafka.getBrokersAsString());

        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
        DefaultKafkaProducerFactory<String, Object> pf = new DefaultKafkaProducerFactory<>(senderProps);
        kafkaTemplate = new KafkaTemplate<>(pf, true);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafka);
        DefaultKafkaConsumerFactory<String, Object> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = cf.createConsumer();
        embeddedKafka.consumeFromEmbeddedTopics(consumer, KAFKA_LISTENER_TOPIC, CLOUD_STREAM_OUTPUT_TOPIC);
    }

    @AfterClass
    public static void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Before
    public void before() {
    }

    @Test
    public void testKafkaLisener() {
        kafkaProducer.send(KAFKA_LISTENER_TOPIC, "foo");
        await().until(() -> "foo".equals(kafkaListenerConsumer.getPayload()));
    }

    @Test
    public void testListenerContainer() {
        kafkaTemplate.send(LISTENER_CONTAINER_TOPIC, "foo");
        await().until(() -> messageListenerContainerConsumer.consumedMessages.contains("foo"));
    }

    @Test
    public void testCloudStream() {
        kafkaTemplate.send(CLOUD_STREAM_INPUT_TOPIC, "foo");

        ConsumerRecord<String, Object> cr = KafkaTestUtils.getSingleRecord(consumer, CLOUD_STREAM_OUTPUT_TOPIC, Duration.ofMillis(3000));

        System.out.println("ConsumerRecord : " + cr.value());
        assertThat(cr.value(), is("FOO"));
    }
}
