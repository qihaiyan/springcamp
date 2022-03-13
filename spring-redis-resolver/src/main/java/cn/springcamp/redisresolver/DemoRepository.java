package cn.springcamp.redisresolver;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemoRepository extends JpaRepository<DemoEntity, Long> {
    @Cacheable(value = "testCache", key = "#p0", unless = "#result == null")
    Optional<DemoEntity> findById(Long id);

    @CacheEvict(value = "testCache", cacheResolver = "customCacheResolver", key = "#p0.id")
    DemoEntity save(DemoEntity entity);
}