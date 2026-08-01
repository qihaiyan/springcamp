package cn.springcamp.springboot.unit.test;

import cn.springcamp.springboot.unit.test.data.MyDomain;
import cn.springcamp.springboot.unit.test.data.MyDomainRepository;
import cn.springcamp.springboot.unit.test.service.MyService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Import(TestRedisConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(
        count = 1,
        controlledShutdown = true,
        topics = { "testEmbeddedIn", "testEmbeddedOut" }
)
@AutoConfigureMockRestServiceServer
class DemoApplicationTest {

    private static final String INPUT_TOPIC = "testEmbeddedIn";
    private static final String OUTPUT_TOPIC = "testEmbeddedOut";
    private static final String GROUP_NAME = "embeddedKafkaApplication";

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        // @EmbeddedKafka auto-sets spring.kafka.bootstrap-servers; point the cloud-stream binder at the same broker.
        registry.add("spring.cloud.stream.kafka.binder.brokers", () -> System.getProperty(EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS));
    }

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    private KafkaTemplate<String, String> kafkaTemplate;

    private Consumer<String, String> consumer;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private MyDomainRepository myDomainRepository;
    @Autowired
    private MyService myService;

    @BeforeEach
    void before() {
        this.mockRestServiceServer.expect(manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("http://someservice/foo")))
                .andRespond(withSuccess("{\"code\": 200}", MediaType.APPLICATION_JSON));

        restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();

        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
        DefaultKafkaProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(senderProps);
        kafkaTemplate = new KafkaTemplate<>(pf, true);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafka);
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = cf.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, OUTPUT_TOPIC);
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void testRemoteCallRest() {
        String resp = restClient.get().uri("/remote").retrieve().body(String.class);
        System.out.println("remote result : " + resp);
        assertThat(resp, is("{\"code\": 200}"));
    }

    @Test
    void testCacheCallRest() {
        String resp = restClient.get().uri("/cache").retrieve().body(String.class);
        System.out.println("cache result : " + resp);
        assertThat(resp, is("ok"));
    }

    @Test
    void testDbCallRest() {
        MyDomain myDomain = new MyDomain();
        myDomain.setName("test");
        LocalDateTime now = LocalDateTime.now();
        myDomain.setCreateTime(now);
        myDomain = myDomainRepository.save(myDomain);
        MyDomain resp = restClient.get().uri("/db?id=" + myDomain.getId()).retrieve().body(MyDomain.class);
        System.out.println("db result : " + resp);
        assertThat(resp.getName(), is("test"));

        ResponseEntity<TestRestResponsePage<MyDomain>> pageResp = restClient.get().uri("/dbpage").retrieve().toEntity(new ParameterizedTypeReference<TestRestResponsePage<MyDomain>>() {
        });
        System.out.println("dbpage result : " + pageResp);
        assertThat(pageResp.getBody().getTotalElements(), is(1L));
        assertThat(pageResp.getBody().get().findFirst().map(d -> d.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()).orElse(Instant.now().getEpochSecond()), is(now.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()));

        String strResp = restClient.get().uri("/dbpage").retrieve().body(String.class);
        System.out.println("dbpage strResp : " + strResp);

        ResponseEntity<TestRestResponseSlice<MyDomain>> sliceResp = restClient.get().uri("/dbpage").retrieve().toEntity(new ParameterizedTypeReference<TestRestResponseSlice<MyDomain>>() {
        });
        System.out.println("dbpage result : " + pageResp);
        assertThat(sliceResp.getBody().getNumberOfElements(), is(1));
    }

    @Test
    void testKafkaSendReceive() {
        kafkaTemplate.send(INPUT_TOPIC, "foo");

        ConsumerRecord<String, String> cr = KafkaTestUtils.getSingleRecord(consumer, OUTPUT_TOPIC, Duration.ofMillis(3000));

        System.out.println("ConsumerRecord : " + cr.value());
        assertThat(cr.value(), is("FOO"));
    }

    @Test
    void reflectionTestUtilsTest() {
        assertThat(myService.getOriginValue(), is("origin"));
        ReflectionTestUtils.setField(myService, "originValue", "test");
        assertThat(myService.getOriginValue(), is("test"));
    }
}
