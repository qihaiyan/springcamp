package cn.springcamp.spring.httpinterface;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class MyClientConfig {

    @Bean
    public MyService myService(RestClient.Builder restClientBuilder) {
        RestClient restClient = restClientBuilder.baseUrl("https://httpbin.org").build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(MyService.class);
    }
}
