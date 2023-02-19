package cn.springcamp.springcloudgateway;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RouteFilterRepository extends ReactiveCrudRepository<RouteFilterEntity, String> {
    Mono<RouteFilterEntity> findByRouteIdAndCode(String routeId, String code);
}