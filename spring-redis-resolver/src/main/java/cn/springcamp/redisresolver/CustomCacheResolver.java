package cn.springcamp.redisresolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.NonNull;

import java.util.Collection;

@Slf4j
public class CustomCacheResolver extends SimpleCacheResolver {

    public CustomCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    @NonNull
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        ParameterNameDiscoverer paramNameDiscoverer = new DefaultParameterNameDiscoverer();
        EvaluationContext evaluationContext = new MethodBasedEvaluationContext(context.getOperation(), context.getMethod(), context.getArgs(), paramNameDiscoverer);
        Expression exp = (new SpelExpressionParser()).parseExpression(((CacheEvictOperation) context.getOperation()).getKey());
        Collection<? extends Cache> caches = super.resolveCaches(context);
        context.getOperation().getCacheNames().forEach(cacheName -> {
            String key = cacheName + ':' + exp.getValue(evaluationContext, String.class);
            log.info("cache key={}", key);
        });
        return caches;
    }
}
