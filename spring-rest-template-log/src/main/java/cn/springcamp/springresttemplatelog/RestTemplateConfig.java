package cn.springcamp.springresttemplatelog;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configuration
public class RestTemplateConfig {

    public CloseableHttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build();
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(
                registry,
                PoolConcurrencyPolicy.STRICT,
                PoolReusePolicy.LIFO,
                TimeValue.NEG_ONE_MILLISECOND,
                null,
                new CustomDnsResolver(2),
                null);

        poolingConnectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(2)).build());
        poolingConnectionManager.setDefaultConnectionConfig(ConnectionConfig.custom().setConnectTimeout(Timeout.ofSeconds(2)).build());

        // set total amount of connections across all HTTP routes
        poolingConnectionManager.setMaxTotal(200);
        // set maximum amount of connections for each http route in pool
        poolingConnectionManager.setDefaultMaxPerRoute(200);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionKeepAlive(TimeValue.ofSeconds(10))
                .setConnectionRequestTimeout(Timeout.ofSeconds(2))
                .setResponseTimeout(Timeout.ofSeconds(2))
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .build();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient()))
                .interceptors(new CustomClientHttpRequestInterceptor())
                .build();
    }

    @Slf4j
    static class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
        @Override
        @NonNull
        public ClientHttpResponse intercept(HttpRequest request, @NonNull byte[] bytes, @NonNull ClientHttpRequestExecution execution) throws IOException {
            log.info("HTTP Method: {}, URI: {}, Headers: {}", request.getMethod(), request.getURI(), request.getHeaders());
            request.getMethod();
            if (request.getMethod().equals(HttpMethod.POST)) {
                log.info("HTTP body: {}", new String(bytes, StandardCharsets.UTF_8));
            }

            ClientHttpResponse response = execution.execute(request, bytes);
            ClientHttpResponse responseWrapper = new BufferingClientHttpResponseWrapper(response);

            String body = StreamUtils.copyToString(responseWrapper.getBody(), StandardCharsets.UTF_8);
            log.info("RESPONSE body: {}", body);

            return responseWrapper;
        }
    }

    static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

        private final ClientHttpResponse response;
        private byte[] body;

        BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
            this.response = response;
        }

        @NonNull
        public HttpStatusCode getStatusCode() throws IOException {
            return this.response.getStatusCode();
        }

        @NonNull
        public String getStatusText() throws IOException {
            return this.response.getStatusText();
        }

        @NonNull
        public HttpHeaders getHeaders() {
            return this.response.getHeaders();
        }

        @NonNull
        public InputStream getBody() throws IOException {
            if (this.body == null) {
                this.body = StreamUtils.copyToByteArray(this.response.getBody());
            }
            return new ByteArrayInputStream(this.body);
        }

        public void close() {
            this.response.close();
        }
    }

    public static class CustomDnsResolver implements DnsResolver {

        private final DnsResolver systemDnsResolver;
        private final Integer connectTimeout;

        public CustomDnsResolver(Integer connectTimeout) {
            this.systemDnsResolver = SystemDefaultDnsResolver.INSTANCE;
            this.connectTimeout = connectTimeout;
        }

        @Override
        public InetAddress[] resolve(final String host) {
            try {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        return systemDnsResolver.resolve(host);
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                }).get(connectTimeout, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String resolveCanonicalHostname(String host) throws UnknownHostException {
            return systemDnsResolver.resolveCanonicalHostname(host);
        }
    }
}
