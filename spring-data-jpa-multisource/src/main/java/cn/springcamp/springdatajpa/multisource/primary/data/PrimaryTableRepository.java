package cn.springcamp.springdatajpa.multisource.primary.data;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrimaryTableRepository extends JpaRepository<PrimaryTable, Long> {
    @Cacheable(value = "testCache", unless="#result == null")
    Optional<PrimaryTable> findById(Long id);
}