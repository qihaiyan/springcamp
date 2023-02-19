package cn.springcamp.springcloudgateway;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Slf4j
@Component
public class UriHostPlaceholderFilter extends AbstractGatewayFilterFactory<UriHostPlaceholderFilter.Config> {
    @Autowired
    private RouteFilterRepository routeFilterRepository;

    public UriHostPlaceholderFilter() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("order");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            String code = exchange.getRequest().getHeaders().getOrDefault("code", new ArrayList<>()).stream().findFirst().orElse("");
            String routeId = exchange.getAttribute(GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR);
            if (StringUtils.hasText(code)) {
                String newurl;
                try {
                    newurl = routeFilterRepository.findByRouteIdAndCode(routeId, code).toFuture().get().getUrl();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                if (StringUtils.hasText(exchange.getRequest().getURI().getQuery())) {
                    newurl = newurl + "?" + exchange.getRequest().getURI().getQuery();
                }
                URI newUri = null;
                try {
                    newUri = new URI(newurl);
                } catch (URISyntaxException e) {
                    log.error("uri error", e);
                }

                exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newUri);
            }
            return chain.filter(exchange);
        }, config.getOrder());
    }

    @Data
    @NoArgsConstructor
    public static class Config {
        private int order;

        public Config(int order) {
            this.order = order;
        }
    }
}
