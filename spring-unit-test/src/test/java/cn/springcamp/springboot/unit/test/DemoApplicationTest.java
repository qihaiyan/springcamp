package cn.springcamp.springboot.unit.test;

import cn.springcamp.springboot.unit.test.data.MyDomain;
import cn.springcamp.springboot.unit.test.data.MyDomainRepository;
import cn.springcamp.springboot.unit.test.service.MyService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestTemplate;

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
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MyDomainRepository myDomainRepository;
    @Autowired
    private MyService myService;

    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void before() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();

        this.mockRestServiceServer.expect(manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("http://someservice/foo")))
                .andRespond(withSuccess("{\"code\": 200}", MediaType.APPLICATION_JSON));

    }

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

    @AfterClass
    public static void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    public void testRemoteCallRest() {
        String resp = testRestTemplate.getForObject("/remote", String.class);
        System.out.println("remote result : " + resp);
        assertThat(resp, is("{\"code\": 200}"));
    }

    @Test
    public void testCacheCallRest() {
        String resp = testRestTemplate.getForObject("/cache", String.class);
        System.out.println("cache result : " + resp);
        assertThat(resp, is("ok"));
    }

    @Test
    public void testDbCallRest() {
        MyDomain myDomain = new MyDomain();
        myDomain.setName("test");
        LocalDateTime now = LocalDateTime.now();
        myDomain.setCreateTime(now);
        myDomain = myDomainRepository.save(myDomain);
        MyDomain resp = testRestTemplate.getForObject("/db?id=" + myDomain.getId(), MyDomain.class);
        System.out.println("db result : " + resp);
        assertThat(resp.getName(), is("test"));

        RequestEntity<Void> requestEntity = RequestEntity.get("/dbpage").build();
        ResponseEntity<TestRestResponsePage<MyDomain>> pageResp = testRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<TestRestResponsePage<MyDomain>>() {
        });
        System.out.println("dbpage result : " + pageResp);
        assertThat(pageResp.getBody().getTotalElements(), is(1L));
        assertThat(pageResp.getBody().get().findFirst().map(d -> d.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()).orElse(Instant.now().getEpochSecond()), is(now.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()));

        String strResp = testRestTemplate.getForObject("/dbpage", String.class);
        System.out.println("dbpage strResp : " + strResp);

        ResponseEntity<TestRestResponseSlice<MyDomain>> sliceResp = testRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<TestRestResponseSlice<MyDomain>>() {
        });
        System.out.println("dbpage result : " + pageResp);
        assertThat(sliceResp.getBody().getNumberOfElements(), is(1));
    }

    @Test
    public void testKafkaSendReceive() {
        kafkaTemplate.send(INPUT_TOPIC, "foo");

        ConsumerRecord<String, String> cr = KafkaTestUtils.getSingleRecord(consumer, OUTPUT_TOPIC, Duration.ofMillis(3000));

        System.out.println("ConsumerRecord : " + cr.value());
        assertThat(cr.value(), is("FOO"));
    }

    @Test
    public void reflectionTestUtilsTest() {
        assertThat(myService.getOriginValue(), is("origin"));
        ReflectionTestUtils.setField(myService, "originValue", "test");
        assertThat(myService.getOriginValue(), is("test"));
    }
}
