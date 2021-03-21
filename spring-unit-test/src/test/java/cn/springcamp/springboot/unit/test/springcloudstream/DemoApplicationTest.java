package cn.springcamp.springboot.unit.test.springcloudstream;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class DemoApplicationTest {

    private static final String INPUT_TOPIC = "testEmbeddedIn";
    private static final String OUTPUT_TOPIC = "testEmbeddedOut";
    private static final String GROUP_NAME = "embeddedKafkaApplication";

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, INPUT_TOPIC, OUTPUT_TOPIC);

    public static EmbeddedKafkaBroker embeddedKafka = embeddedKafkaRule.getEmbeddedKafka();

    private static KafkaTemplate<String, String> kafkaTemplate;

    private static Consumer<String, String> consumer;

    @BeforeClass
    public static void setup() {
        System.setProperty("spring.cloud.stream.kafka.binder.brokers", embeddedKafka.getBrokersAsString());

        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
        DefaultKafkaProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(senderProps);
        kafkaTemplate = new KafkaTemplate<>(pf, true);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafka);
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = cf.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, OUTPUT_TOPIC);
    }

    @After
    public void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    public void testSendReceive() {
        kafkaTemplate.send(INPUT_TOPIC, "foo");

        ConsumerRecord<String, String> cr = KafkaTestUtils.getSingleRecord(consumer, OUTPUT_TOPIC);

        System.out.println("ConsumerRecord : " + cr.value());
    }

}
